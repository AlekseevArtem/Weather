package ru.job4j.weather.presenter

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*

import moxy.InjectViewState
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import ru.job4j.weather.R

import ru.job4j.weather.store.Answer
import ru.job4j.weather.store.Day
import ru.job4j.weather.store.RoomDB


import ru.job4j.weather.view.MainActivityView
import java.text.SimpleDateFormat
import java.util.*


@InjectViewState
class MainActivityPresenter(private val context: Context) : MvpPresenter<MainActivityView>() {
    private var answer: Answer? = null
    private val days: MutableList<Day> = mutableListOf()
    private val db: RoomDB = RoomDB.getDatabase(context)
    private var day: Int = 0
    private var hour: Int = 0

    private interface JsonAnswerHolderApi {
        @GET("forecast")
        fun getAnswer(
                @Query("lat") lat: Double,
                @Query("lon") long: Double,
                @Query("appid") key: String,
                @Query("units") units: String = "metric",
        ): Call<Answer>
    }

    fun getAnswerFromDB(type: Int = Answer.GEO) {
       if (answer == null) {
           GlobalScope.launch {
               answer = db.getAnswerDao().getAnswer(type)
               generateDays()
               withContext(Dispatchers.Main) {
                   viewState.successAnswer(days, answer!!.list[getPositionOfDetails()], answer!!.city, day, hour)
               }
           }
        }
    }

    fun callApi(coordinates: LatLng, type: Int) {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val call: Call<Answer> = retrofit
                .create(JsonAnswerHolderApi::class.java)
                .getAnswer(coordinates.latitude, coordinates.longitude, context.getString(R.string.geo_api_key))
        call.enqueue(object : Callback<Answer> {
            override fun onResponse(call: Call<Answer>, response: Response<Answer>) {
                if (response.isSuccessful) {
                    response.body()!!.answerId = type
                    answer = response.body()
                    generateDays()
                    day = 0
                    hour = 0
                    viewState.successAnswer(days, answer!!.list[getPositionOfDetails()], answer!!.city, day, hour)
                    GlobalScope.launch { db.getAnswerDao().insertAnswer(response.body()!!) }
                } else {
                    viewState.successWithError(response.code())
                }
            }

            override fun onFailure(call: Call<Answer>, t: Throwable): Unit =
                    viewState.failedAnswer(t.message.toString())
        })
    }

    fun changeCurrentDayAndHour(day: Int, hour: Int) {
        if (day != -1) this.day = day
        this.hour = hour
        viewState.updatePositions(this.day, this.hour, answer!!.list[getPositionOfDetails()])
    }

    private fun getPositionOfDetails(): Int{
        var position = hour
        days.forEachIndexed { index, day -> if (index < this.day) position += day.hours.size }
        return position
    }

    private fun generateDays() {
        days.clear()
        var tempDay = ""
        var sumTemp = 0.0
        var countTemp = 0
        var worstIcon = 0
        answer?.list?.forEach {
            val dateInMillis = it.date * 1000
            SimpleDateFormat("d").format(dateInMillis).also { day ->
                if (tempDay != day) {
                    if (tempDay != "") {
                        recordTempAndIconOnTheLastDay(sumTemp, countTemp, worstIcon)
                        sumTemp = 0.0
                        countTemp = 0
                        worstIcon = 0
                    }
                    days.add(Day(date = Date(dateInMillis)))
                    tempDay = day
                }
            }
            val hour = SimpleDateFormat("H").format(dateInMillis).toInt()
            days.last().hours.add("$hour:00")
            if (hour > 9) {
                sumTemp += it.main.temp
                countTemp++
                val thisIcon = it.weather[0].icon.dropLast(1).toInt()
                if (worstIcon < thisIcon) worstIcon = thisIcon
            }
        }
        recordTempAndIconOnTheLastDay(sumTemp, countTemp, worstIcon, last = true)
    }

    private fun recordTempAndIconOnTheLastDay(sumTemp: Double, countTemp: Int, worstIcon: Int, last: Boolean = false): Unit =
            days.last().run {
                if (last) {
                    temperature = "${answer!!.list.last().main.temp.toInt()}°"
                    icon = answer!!.list.last().weather[0].icon
                } else {
                    temperature = "${(sumTemp / countTemp).toInt()}°"
                    icon = if (worstIcon < 10) "0${worstIcon}d" else "${worstIcon}d"
                }
            }
}
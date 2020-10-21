package ru.job4j.weather.presenter

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*

import moxy.InjectViewState
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.job4j.weather.App
import ru.job4j.weather.di.RemoteModule
import ru.job4j.weather.store.Answer
import ru.job4j.weather.store.Day
import ru.job4j.weather.store.RoomDB
import ru.job4j.weather.view.MainActivityView
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by Artem Alexeev on 21.10.2020.
 * Presenter for MainActivity
 */
@InjectViewState
class MainActivityPresenter : MvpPresenter<MainActivityView>() {
    private var answer: Answer? = null
    private val days: MutableList<Day> = mutableListOf()
    @Inject lateinit var db: RoomDB
    @Inject lateinit var jsonAnswerHolderApi: RemoteModule.JsonAnswerHolderApi
    private var day: Int = 0
    private var hour: Int = 0

    init { App.dagger?.inject(this) }

    fun getAnswerFromDB(type: Int = Answer.GEO) {
        if (answer == null) {
            GlobalScope.launch {
                answer = db.getAnswerDao().getAnswer(type)
                answer?.let {
                    generateDays()
                    withContext(Dispatchers.Main) {
                        viewState.successAnswer(days, it.list[getPositionOfDetails()], it.city, day, hour)
                    }
                }
            }
        }
    }

    fun callApi(coordinates: LatLng, type: Int, key: String) {
        jsonAnswerHolderApi
                .getAnswer(coordinates.latitude, coordinates.longitude, key)
                .enqueue(object : Callback<Answer> {
                    override fun onResponse(call: Call<Answer>, response: Response<Answer>) {
                        if (response.isSuccessful) {
                            answer = response.body()
                            answer!!.answerId = type
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

    private fun getPositionOfDetails(): Int {
        var position = hour
        days.forEachIndexed { index, day -> if (index < this.day) position += day.hours.size }
        return position
    }


    /**
     * Method for generate days. Used only one loop.
     * Generates Days from Answer. Answer has 40 dates as Integer(seconds).
     * Count average temperature and find worst weather for hours bigger than 9.
     */
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

    private fun recordTempAndIconOnTheLastDay(sumTemp: Double, countTemp: Int, worstIcon: Int, last: Boolean = false) {
        if(answer != null){
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
    }
}
package ru.job4j.weather.retrofit

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.job4j.weather.R
import ru.job4j.weather.store.Answer

class RetrofitForJSON(private val mContext: Context, private val mCoord: LatLng, private val mType: Int = 0) {
    private val mJsonHolder: JsonAnswerHolderApi

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        mJsonHolder = retrofit.create(JsonAnswerHolderApi::class.java)
    }

    interface GetAnswerFromAPI {
        fun successAnswer(response: Boolean, body: Answer?, code: Int)
        fun failedAnswer(response: String)
    }

    fun callForAnswer() {
        val call: Call<Answer> = mJsonHolder.getAnswer(mCoord.latitude,
                mCoord.longitude, mContext.getString(R.string.geo_api_key))
        call.enqueue(object : Callback<Answer> {
            override fun onResponse(call: Call<Answer>, response: Response<Answer>) {
                response.body()?.answerId = mType
                (mContext as GetAnswerFromAPI)
                        .successAnswer(response.isSuccessful, response.body(), response.code())
            }

            override fun onFailure(call: Call<Answer>, t: Throwable) {
                (mContext as GetAnswerFromAPI).failedAnswer(t.message.toString())
            }
        })
    }


}
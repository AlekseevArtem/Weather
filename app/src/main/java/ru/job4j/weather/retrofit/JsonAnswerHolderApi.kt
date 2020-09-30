package ru.job4j.weather.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.job4j.weather.store.Answer

interface JsonAnswerHolderApi {
    @GET("forecast")
    fun getAnswer(@Query("lat") lat: Double,
                  @Query("lon") long: Double,
                  @Query("appid") key: String,
                  @Query("units") units: String = "metric"
    ): Call<Answer>
}
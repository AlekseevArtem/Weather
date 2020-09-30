package ru.job4j.weather.store

import androidx.room.*

@Dao
interface AnswerDao {
    @Query("SELECT * FROM answer WHERE answerId IS :type")
    suspend fun getAnswer(type: Int): Answer?

    @Insert
    suspend fun insertAnswer(answer: Answer)

    @Update
    suspend fun updateAnswer(answer: Answer)
}

@Dao
interface DetailsDao {
    @Query("SELECT * FROM details WHERE answerIdLink IS :answerId")
    suspend fun getDetails(answerId: Int): List<Answer.Details>

    @Insert
    suspend fun insertDetails(details: List<Answer.Details>)

    @Update
    suspend fun updateDetails(details: List<Answer.Details>)
}

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE detailsIdLink IS :detailsId")
    suspend fun getWeather(detailsId: Int): List<Answer.Details.Weather>

    @Insert
    suspend fun insertWeather(weather: List<Answer.Details.Weather>)

    @Update
    suspend fun updateWeather(weather: List<Answer.Details.Weather>)
}
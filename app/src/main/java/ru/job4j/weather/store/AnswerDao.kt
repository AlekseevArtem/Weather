package ru.job4j.weather.store

import androidx.room.*

@Dao
interface AnswerDao {
    @Query("SELECT * FROM answer WHERE answerId IS :type")
    suspend fun getAnswer(type: Int): Answer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: Answer)
}
package ru.job4j.weather.store

import androidx.room.*

/**
 * Created by Artem Alexeev on 21.10.2020.
 * Dao for RoomDB
 */
@Dao
interface AnswerDao {
    @Query("SELECT * FROM answer WHERE answerId IS :type")
    suspend fun getAnswer(type: Int): Answer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: Answer)
}
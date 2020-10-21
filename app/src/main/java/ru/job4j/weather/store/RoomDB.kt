package ru.job4j.weather.store

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Created by Artem Alexeev on 21.10.2020.
 * Database for Answers
 */
@Database(entities = [Answer::class], version = 1)
@TypeConverters(DetailsListConverter::class)
abstract class RoomDB : RoomDatabase() {
    abstract fun getAnswerDao(): AnswerDao
}


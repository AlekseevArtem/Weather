package ru.job4j.weather.store

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Answer::class], version = 1)
@TypeConverters(DetailsListConverter::class)
abstract class RoomDB : RoomDatabase() {
    abstract fun getAnswerDao(): AnswerDao
}


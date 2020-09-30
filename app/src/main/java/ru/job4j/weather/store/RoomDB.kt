package ru.job4j.weather.store

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Answer::class, Answer.Details::class, Answer.Details.Weather::class], version = 1)
abstract class RoomDB : RoomDatabase() {
    abstract fun getAnswerDao(): AnswerDao
    abstract fun getWeatherDao(): WeatherDao
    abstract fun getDetailDao(): DetailsDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDB? = null

        fun getDatabase(context: Context): RoomDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        RoomDB::class.java,
                        "weatherVersion1"
                ).build()
                return INSTANCE as RoomDB
            }
        }
    }
}


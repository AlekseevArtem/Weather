package ru.job4j.weather.store

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Answer::class], version = 1)
@TypeConverters(DetailsListConverter::class)
abstract class RoomDB : RoomDatabase() {
    abstract fun getAnswerDao(): AnswerDao

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


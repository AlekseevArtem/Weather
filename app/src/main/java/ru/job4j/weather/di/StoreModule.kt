package ru.job4j.weather.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.job4j.weather.store.RoomDB
import javax.inject.Singleton


@Module
class StoreModule {
    @Singleton @Provides fun providesRoomDB(context: Context): RoomDB =
            Room.databaseBuilder(
                    context,
                    RoomDB::class.java,
                    "weatherVersion1"
            ).build()
}
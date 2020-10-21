package ru.job4j.weather.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.job4j.weather.App
import javax.inject.Singleton

/**
 * Created by Artem Alexeev on 21.10.2020.
 * AppModule
 */
@Module
class AppModule(val app: App) {
    @Singleton @Provides fun providesApplication(): Application = app
    @Singleton @Provides fun providesContext(application: Application): Context = application.applicationContext
}
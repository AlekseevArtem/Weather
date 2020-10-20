package ru.job4j.weather

import android.app.Application
import ru.job4j.weather.di.AppComponent
import ru.job4j.weather.di.AppModule
import ru.job4j.weather.di.DaggerAppComponent

class App : Application() {
    companion object {
        var dagger: AppComponent? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        dagger = DaggerAppComponent.builder().appModule(AppModule(app = this@App)).build()
    }
}
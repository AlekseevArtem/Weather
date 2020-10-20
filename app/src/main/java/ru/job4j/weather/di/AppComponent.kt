package ru.job4j.weather.di

import dagger.Component
import ru.job4j.weather.presenter.MainActivityPresenter
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, StoreModule::class, RemoteModule::class])
interface AppComponent {
    fun inject(mainActivityPresenter: MainActivityPresenter)
}
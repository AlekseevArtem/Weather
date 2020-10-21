package ru.job4j.weather.di

import dagger.Component
import ru.job4j.weather.presenter.MainActivityPresenter
import javax.inject.Singleton

/**
 * Created by Artem Alexeev on 21.10.2020.
 * AppComponent. Intermediary between @Inject and Modules
 */
@Singleton
@Component(modules = [AppModule::class, StoreModule::class, RemoteModule::class])
interface AppComponent {
    fun inject(mainActivityPresenter: MainActivityPresenter)
}
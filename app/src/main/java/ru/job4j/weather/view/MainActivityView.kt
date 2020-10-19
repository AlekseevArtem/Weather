package ru.job4j.weather.view

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.job4j.weather.store.Answer
import ru.job4j.weather.store.Day

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface MainActivityView : MvpView {
    @StateStrategyType(value = AddToEndStrategy::class)
    fun successAnswer(days: List<Day>, details: Answer.Details, city: Answer.City, day: Int, hour: Int)
    fun successWithError(code: Int)
    fun failedAnswer(response: String)
    @StateStrategyType(value = AddToEndStrategy::class)
    fun updatePositions(day: Int, hour: Int, details: Answer.Details)
}
package ru.job4j.weather.fragments

/**
 * Created by Artem Alexeev on 11.10.2020.
 * Interface for callback from fragments to activity
 */
interface CallbackToActivity {
    fun updatePositionsFromFragment(day: Int, hour: Int)
}
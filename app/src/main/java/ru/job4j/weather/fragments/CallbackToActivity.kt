package ru.job4j.weather.fragments

interface CallbackToActivity {
    fun updatePositions(day: Int, hour: Int)
}
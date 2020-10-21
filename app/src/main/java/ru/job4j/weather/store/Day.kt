package ru.job4j.weather.store

import java.util.*

/**
 * Created by Artem Alexeev on 21.10.2020.
 * Data class Day.
 * List of days generates in MainActivityPresenter
 */
data class Day(val date: Date, var temperature: String = "", var icon: String = "", var hours: MutableList<String> = mutableListOf())
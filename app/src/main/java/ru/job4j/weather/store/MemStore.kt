package ru.job4j.weather.store

import java.text.SimpleDateFormat
import java.util.*

class MemStore {
    companion object {
        private lateinit var INSTANCE: MemStore

        fun getMemStore(): MemStore {
            if (!this::INSTANCE.isInitialized) {
                INSTANCE = MemStore()
            }
            return INSTANCE
        }
    }

    var answer: Answer? = null
    var days: MutableList<Day> = mutableListOf()

    fun saveAnswer(answer: Answer) {
        this.answer = answer
        days.clear()
        var tempDay = ""
        var sumTemp = 0.0
        var countTemp = 0
        var worstIcon = 0
        answer.list.forEach {
            val dateInMillis = it.date * 1000
            SimpleDateFormat("d").format(dateInMillis).also { day ->
                if (tempDay != day) {
                    if (tempDay != "") {
                        days.last().temperature = "${(sumTemp / countTemp).toInt()}°"
                        days.last().icon = if (worstIcon < 10) "0${worstIcon}d" else "${worstIcon}d"
                        worstIcon = 0
                        sumTemp = 0.0
                        countTemp = 0
                    }
                    days.add(Day(date = Date(dateInMillis)))
                    tempDay = day
                }
            }
            val hour = SimpleDateFormat("H").format(dateInMillis).toInt()
            days.last().hours.add("$hour:00")
            if (hour > 9) {
                sumTemp += it.main.temp
                countTemp++
                val thisIcon = it.weather[0].icon.dropLast(1).toInt()
                if (worstIcon < thisIcon) worstIcon = thisIcon
            }
        }
        days.last().apply {
            if(countTemp != 0){
                temperature = "${(sumTemp / countTemp).toInt()}°"
                icon = if (worstIcon < 10) "0${worstIcon}d" else "${worstIcon}d"
            } else {
                temperature = "${answer.list.last().main.temp.toInt()}°"
                icon = answer.list.last().weather[0].icon
            }
        }


    }
}
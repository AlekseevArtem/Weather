package ru.job4j.weather.store

import android.content.Context

class SimpleDB {
    private lateinit var answerDatabase: RoomDB

    companion object {
        private lateinit var INSTANCE: SimpleDB

        fun getDatabase(context: Context): SimpleDB {
            if (!this::INSTANCE.isInitialized) {
                INSTANCE = SimpleDB()
                INSTANCE.answerDatabase = RoomDB.getDatabase(context)
            }
            return INSTANCE
        }
    }

    suspend fun getAnswer(type: Int): Answer? {
        val result = answerDatabase.getAnswerDao().getAnswer(type)
        result?.list = answerDatabase.getDetailDao().getDetails(type)
        result?.list?.forEach { it.weather = answerDatabase.getWeatherDao().getWeather(it.detailsId) }
        return result
    }

    suspend fun insertAnswer(answer: Answer) {
        answerDatabase.apply {
            getAnswerDao().insertAnswer(answer)
            var temp = 0
            answer.list.forEach { it.detailsId = temp++ }
            getDetailDao().insertDetails(answer.list)
            answer.list.forEach {
                it.weather.forEach { weather -> weather.detailsIdLink = it.detailsId }
                getWeatherDao().insertWeather(it.weather)
            }
        }
    }

    suspend fun updateAnswer(answer: Answer) {
        answerDatabase.apply {
            getAnswerDao().updateAnswer(answer)
            var temp = 0
            answer.list.forEach { it.detailsId = temp++ }
            getDetailDao().updateDetails(answer.list)
            answer.list.forEach {
                it.weather.forEach { weather -> weather.detailsIdLink = it.detailsId }
                getWeatherDao().updateWeather(it.weather)
            }
        }
    }
}
package ru.job4j.weather.store

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.lang.reflect.Type

class DetailsListConverter : Serializable {
    @TypeConverter
    fun fromDetailsList(details: List<Answer.Details>?): String = Gson().toJson(details)

    @TypeConverter
    fun toDetailsList(json: String?): List<Answer.Details>? =
            Gson().fromJson<List<Answer.Details>>(json, object : TypeToken<List<Answer.Details>>() {}.type)
}
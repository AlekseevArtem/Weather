package ru.job4j.weather.store

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

/**
 * Created by Artem Alexeev on 21.10.2020.
 * Converter for list of Details to Gson(and back)
 */
class DetailsListConverter : Serializable {
    @TypeConverter
    fun fromDetailsList(details: List<Answer.Details>?): String = Gson().toJson(details)

    @TypeConverter
    fun toDetailsList(json: String?): List<Answer.Details>? =
            Gson().fromJson<List<Answer.Details>>(json, object : TypeToken<List<Answer.Details>>() {}.type)
}
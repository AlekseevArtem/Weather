package ru.job4j.weather.store

import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity(tableName = "answer")
data class Answer(
        @PrimaryKey(autoGenerate = false) var answerId: Int = 0,
        @SerializedName("cod") var cod: String = "",
        @SerializedName("message") var message: Int = 0,
        @SerializedName("cnt") var cnt: Int = 0,
        @Embedded var city: City = City(),
        @Ignore var list: List<Details> = listOf()
) {
    @Entity(foreignKeys = [ForeignKey(
            entity = Answer::class,
            parentColumns = ["answerId"],
            childColumns = ["answerIdLink"])],
            tableName = "details")
    data class Details(
            @PrimaryKey(autoGenerate = false) var detailsId: Int = 0,
            var answerIdLink: Int = 0,
            @SerializedName("dt") var date: Long = 0L,
            @Embedded var main: Main = Main(),
            @Ignore var weather: List<Weather> = mutableListOf(),
            @Embedded var clouds: Clouds = Clouds(),
            @Embedded var wind: Wind = Wind(),
            var visibility: Int = 0,
            var pop: Double = 0.0,
            @Embedded var rain: Rain = Rain(),
            @Embedded var sys: Sys = Sys(),
            var dt_txt: String = "",
    ) {
        data class Main(
                var temp: Double = 0.0,
                var feels_like: Double = 0.0,
                var temp_min: Double = 0.0,
                var temp_max: Double = 0.0,
                var pressure: Int = 0,
                var sea_level: Int = 0,
                var grnd_level: Int = 0,
                var humidity: Int = 0,
                var temp_kf: Double = 0.0,
        )

        @Entity(foreignKeys = [ForeignKey(
                entity = Details::class,
                parentColumns = ["detailsId"],
                childColumns = ["detailsIdLink"])],
                tableName = "weather")
        data class Weather(
                @PrimaryKey(autoGenerate = true) var weatherId: Int = 0,
                var id: Int = 0,
                var detailsIdLink: Int = 0,
                var main: String = "",
                var description: String = "",
                var icon: String = "",
        )

        data class Clouds(
                var all: Int = 0,
        )

        data class Wind(
                var speed: Double = 0.0,
                var deg: Int = 0,
        )

        data class Rain(
                @SerializedName("3h") var threeHours: Double = 0.0,
        )

        data class Sys(
                var pod: String = "",
        )
    }

    data class City(
            @SerializedName("id") var cityId: Int = 0,
            var name: String = "",
            @SerializedName("coord") @Embedded var coordinates: Coordinates = Coordinates(),
            var country: String = "",
            var timezone: Int = 0,
            var sunrise: Long = 0L,
            var sunset: Long = 0L,
    ) {
        data class Coordinates(
                var lat: Double = 0.0,
                var lon: Double = 0.0,
        )
    }
}



package ru.job4j.weather.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_main_info.*
import ru.job4j.weather.R
import ru.job4j.weather.store.Answer

/**
 * Created by Artem Alexeev on 11.10.2020.
 * Fragment for display main information about weather in selected day and hour
 * Ruled by MainActivity
 */
class FragmentMainInfo : Fragment() {
    private var details: Answer.Details? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main_info, container, false)

    override fun onStart() {
        main.visibility = View.INVISIBLE
        details?.let { updateUI(it) }
        super.onStart()
    }

    fun updateUI(details: Answer.Details) {
        main.visibility = View.VISIBLE
        this.details = details
        val feelsLike = details.main.feels_like
        val temp = details.main.temp
        fragment_main_info_realfeel.text = if (feelsLike.toString()[0] != '-' && feelsLike.toString()[0] != '0') "+${feelsLike.toInt()}°" else "${feelsLike.toInt()}°"
        fragment_main_info_cloud.text = "${details.clouds.all}%"
        fragment_main_info_temperature.text = if (temp.toString()[0] != '-' && temp.toString()[0] != '0') "+${temp.toInt()}°" else "${temp.toInt()}°"
        fragment_main_info_pressure.text = "${details.main.pressure.toDouble() * 0.75} ${getString(R.string.millimeters_of_mercury)}"
        fragment_main_info_probOfPrec.text = "${(details.pop*100).toInt()}%"
        fragment_main_info_wind.text = "${details.wind.speed} ${getString(R.string.wind_north)}, ${windDirection(details.wind.deg)}"
        Picasso.with(activity)
                .load("https://openweathermap.org/img/wn/${details.weather[0].icon}@2x.png")
                .into(fragment_main_info_icon)
    }

    private fun windDirection(deg: Int) =
            when (deg) {
                in 0..22 -> getString(R.string.wind_north)
                in 23..67 -> getString(R.string.wind_northeast)
                in 68..112 -> getString(R.string.wind_east)
                in 113..157 -> getString(R.string.wind_southeast)
                in 158..202 -> getString(R.string.wind_south)
                in 203..247 -> getString(R.string.wind_southwest)
                in 248..292 -> getString(R.string.wind_west)
                in 293..337 -> getString(R.string.wind_northwest)
                in 338..360 -> getString(R.string.wind_north)
                else -> ""
            }
}
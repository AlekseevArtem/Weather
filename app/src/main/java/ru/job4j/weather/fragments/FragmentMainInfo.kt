package ru.job4j.weather.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.fragment_main_info.*
import ru.job4j.weather.R
import ru.job4j.weather.store.Answer
import ru.job4j.weather.store.IconsFileCache

class FragmentMainInfo : Fragment() {
    private var mDetails: Answer.Details? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
        return inflater.inflate(R.layout.fragment_main_info, container, false)
    }

    override fun onStart() {
        main.visibility = View.INVISIBLE
        mDetails?.let { updateUI(it) }
        super.onStart()
    }

    fun updateUI(details: Answer.Details) {
        main.visibility = View.VISIBLE
        mDetails = details
        val feelsLike = details.main.feels_like
        val temp = details.main.temp
        fragment_main_info_realfeel.text = if (feelsLike.toString()[0] != '-' && feelsLike.toString()[0] != '0') "+${feelsLike.toInt()}°" else "${feelsLike.toInt()}°"
        fragment_main_info_cloud.text = "${details.clouds.all}%"
        fragment_main_info_temperature.text = if (temp.toString()[0] != '-' && temp.toString()[0] != '0') "+${temp.toInt()}°" else "${temp.toInt()}°"
        fragment_main_info_pressure.text = "${details.main.pressure.toDouble() * 0.75} мм рт. ст."
        fragment_main_info_probOfPrec.text = "${details.pop}%"
        fragment_main_info_wind.text = "${details.wind.speed} м/с, ${windDirection(details.wind.deg)}"
        IconsFileCache(activity!!).uploadIcon(details.weather[0].icon, fragment_main_info_icon)
    }

    private fun windDirection(deg: Int) =
            when (deg) {
                in 0..22 -> "C"
                in 23..67 -> "СВ"
                in 68..112 -> "В"
                in 113..157 -> "ЮВ"
                in 158..202 -> "Ю"
                in 203..247 -> "ЮЗ"
                in 248..292 -> "З"
                in 293..337 -> "СЗ"
                in 338..360 -> "С"
                else -> ""
            }
}
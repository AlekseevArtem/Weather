package ru.job4j.weather.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.day_item.view.*
import ru.job4j.weather.R
import ru.job4j.weather.store.Day
import java.text.SimpleDateFormat

/**
 * Created by Artem Alexeev on 11.10.2020.
 * Fragment with RecyclerView for display all available days
 * Ruled by MainActivity
 */
class FragmentDaysRV : Fragment() {
    private lateinit var viewForDays: RecyclerView
    private lateinit var callback: CallbackToActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_recyclerview, container, false)
        viewForDays = view.findViewById(R.id.list_of_items)
        val orientation = if (resources.configuration.orientation ==
                Configuration.ORIENTATION_LANDSCAPE) RecyclerView.VERTICAL else RecyclerView.HORIZONTAL
        viewForDays.layoutManager = LinearLayoutManager(activity, orientation, false)
        callback = context as CallbackToActivity
        return view
    }

    fun updateUI(days: List<Day>, position: Int) {
        viewForDays.adapter = DayAdapter(days, position)
        viewForDays.scrollToPosition(position)
    }

    class DayHolder(inflater: LayoutInflater, parent: ViewGroup?, var mPosition: Int = 0) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.day_item, parent, false)) {

        fun bind(day: Day, position: Int) {
            mPosition = position
            itemView.apply {
                fragment_days_data.text = SimpleDateFormat("EEE, d MMM ").format(day.date)
                fragment_day_temperature.text = if (day.temperature[0] != '-') "+${day.temperature}" else day.temperature
                setOnClickListener { onClick() }
            }
            Picasso.with(itemView.context)
                    .load("https://openweathermap.org/img/wn/${day.icon}@2x.png")
                    .into(itemView.fragment_day_icon)
        }

        fun setActivated(): Unit =
            itemView.findViewById<ConstraintLayout>(R.id.day)
                        .setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.background))

        private fun onClick(): Unit =
            (itemView.context as CallbackToActivity).updatePositionsFromFragment(mPosition, 0)
    }

    class DayAdapter(private val mDays: List<Day>, private val mPosition: Int) : RecyclerView.Adapter<DayHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayHolder =
                DayHolder(LayoutInflater.from(parent.context), parent)

        override fun onBindViewHolder(holder: DayHolder, position: Int) {
            holder.bind(mDays[position], position)
            if (position == mPosition) holder.setActivated()
        }

        override fun getItemCount(): Int = mDays.size
    }
}

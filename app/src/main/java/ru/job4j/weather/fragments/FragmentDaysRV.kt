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
import kotlinx.android.synthetic.main.day_item.view.*
import ru.job4j.weather.R
import ru.job4j.weather.store.Day
import ru.job4j.weather.store.IconsFileCache
import java.text.SimpleDateFormat

class FragmentDaysRV : Fragment() {
    private lateinit var mViewForDays: RecyclerView
    private var mDays: MutableList<Day> = mutableListOf()
    private var mPosition = 0
    private lateinit var callback: CallbackToActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_recyclerview, container, false)
        retainInstance = true
        mViewForDays = view.findViewById(R.id.list_of_items)
        val orientation = if (resources.configuration.orientation ==
                Configuration.ORIENTATION_LANDSCAPE) RecyclerView.VERTICAL else RecyclerView.HORIZONTAL
        mViewForDays.layoutManager = LinearLayoutManager(activity, orientation, false)
        mViewForDays.adapter = DayAdapter(mDays,mPosition)
        callback = context as CallbackToActivity
        return view
    }

    fun updateUI(days: MutableList<Day>, position: Int) {
        mPosition = position
        mDays.clear()
        mDays.addAll(days)
        mViewForDays.adapter = DayAdapter(mDays,mPosition)
    }

    class DayHolder(inflater: LayoutInflater, parent: ViewGroup?, var mPosition: Int = 0) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.day_item, parent, false)) {

        fun bind(day: Day, position: Int) {
            mPosition = position
            itemView.fragment_days_data.text = SimpleDateFormat("EEE, d MMM ").format(day.date)
            itemView.fragment_day_temperature.text = if (day.temperature[0] != '-') "+${day.temperature}" else day.temperature
            IconsFileCache(itemView.context).uploadIcon(day.icon, itemView.fragment_day_icon)
            itemView.setOnClickListener { onClick() }
        }

        fun setActivated() {
            itemView.findViewById<ConstraintLayout>(R.id.day).setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.background))
        }

        private fun onClick() {
            (itemView.context as CallbackToActivity).updatePositions(mPosition, 0)
        }
    }

    class DayAdapter(private val mDays: List<Day>, private val mPosition: Int) : RecyclerView.Adapter<DayHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayHolder =
                DayHolder(LayoutInflater.from(parent.context), parent)

        override fun onBindViewHolder(holder: DayHolder, position: Int) {
            holder.bind(mDays[position], position)
            if (position == mPosition) {
                holder.setActivated()
            }
        }

        override fun getItemCount(): Int = mDays.size
    }
}

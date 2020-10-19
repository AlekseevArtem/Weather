package ru.job4j.weather.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import ru.job4j.weather.R

/**
 * Created by Artem Alexeev on 11.10.2020.
 * Fragment with RecyclerView for display all available hours in selected day
 * Ruled by MainActivity
 */
class FragmentHoursRV : Fragment() {
    private lateinit var viewForHours: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_recyclerview, container, false)
        viewForHours = view.findViewById(R.id.list_of_items)
        viewForHours.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        return view
    }

    fun updateUI(hours: List<String>, position: Int) {
        viewForHours.adapter = HoursAdapter(hours, position)
    }

    class HoursHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.hour_item, parent, false)) {

        fun bind(hour: String, position: Int) {
            itemView.findViewById<MaterialTextView>(R.id.fragment_hours_hour).text = hour
            itemView.setOnClickListener { onClick(position) }
        }

        fun setActivated(): Unit =
                itemView.findViewById<ConstraintLayout>(R.id.hour).setBackgroundColor(getColor(itemView.context, R.color.background))

        private fun onClick(position: Int): Unit =
                (itemView.context as CallbackToActivity).updatePositionsFromFragment(-1, position)
    }

    class HoursAdapter(private val hours: List<String>, private val position: Int) : RecyclerView.Adapter<HoursHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursHolder =
                HoursHolder(LayoutInflater.from(parent.context), parent)

        override fun onBindViewHolder(holder: HoursHolder, position: Int) {
            holder.bind(hours[position], position)
            if (position == this.position) {
                holder.setActivated()
            }
        }

        override fun getItemCount() = hours.size
    }

}
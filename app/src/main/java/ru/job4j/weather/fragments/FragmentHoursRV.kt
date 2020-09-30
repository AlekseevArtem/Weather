package ru.job4j.weather.fragments

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import ru.job4j.weather.R

class FragmentHoursRV : Fragment() {
    private lateinit var mViewForHours: RecyclerView
    private val mHours: MutableList<String> = mutableListOf()
    private val mPosition: MutableList<Int> = mutableListOf(0)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_recyclerview, container, false)
        retainInstance = true
        mViewForHours = view.findViewById(R.id.list_of_items)
        mViewForHours.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        mViewForHours.adapter = HoursAdapter(mHours, mPosition)
        return view
    }

    fun updateUI(hours: List<String>, position: Int) {
        this.mHours.clear()
        this.mHours.addAll(hours)
        mPosition[0] = position
        mViewForHours.adapter = HoursAdapter(mHours, mPosition)
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
                (itemView.context as CallbackToActivity).updatePositions(-1, position)
    }

    class HoursAdapter(private val mHours: List<String>, private val mPosition: List<Int>) : RecyclerView.Adapter<HoursHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursHolder =
                HoursHolder(LayoutInflater.from(parent.context), parent)

        override fun onBindViewHolder(holder: HoursHolder, position: Int) {
            holder.bind(mHours[position], position)
            if (position == mPosition[0]) {
                holder.setActivated()
            }
        }

        override fun getItemCount() = mHours.size
    }

}
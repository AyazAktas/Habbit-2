package com.ayaz.habbit.ui.habbit.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayaz.habbit.R
import com.ayaz.habbit.data.local.entity.Habit

class HabitAdapter(
    private val onHabitChecked: (Habit, Boolean) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    private val habitList = mutableListOf<Habit>()
    private val completedIds = mutableSetOf<Int>() // hangi habitler işaretli tutulacak

    fun setHabits(newList: List<Habit>, completed: Set<Int>) {
        habitList.clear()
        habitList.addAll(newList)

        completedIds.clear()
        completedIds.addAll(completed)

        notifyDataSetChanged()
    }

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.imageViewHabitIcon)
        private val tvName: TextView = itemView.findViewById(R.id.textViewHabitName)
        private val tvDescription: TextView = itemView.findViewById(R.id.textViewHabitDescription)
        private val cbDone: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bind(habit: Habit) {
            ivIcon.setImageResource(habit.iconResId)
            tvName.text = habit.name
            tvDescription.text = habit.description

            cbDone.setOnCheckedChangeListener(null) // listener sıfırla

            // 🔹 DB’den gelen set’e göre işaretle
            cbDone.isChecked = completedIds.contains(habit.id)

            cbDone.setOnCheckedChangeListener { _, isChecked ->
                onHabitChecked(habit, isChecked)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habitList[position])
    }

    override fun getItemCount(): Int = habitList.size
}

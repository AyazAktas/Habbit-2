package com.example.habbit.ui.habbit.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.habbit.R
import com.example.habbit.data.local.entity.Habit

class HabitAdapter : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    private val habitList = mutableListOf<Habit>()

    fun setHabits(newList: List<Habit>) {
        habitList.clear()
        habitList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.imageViewHabitIcon)
        private val tvName: TextView = itemView.findViewById(R.id.textViewHabitName)
        private val tvDescription: TextView = itemView.findViewById(R.id.textViewHabitDescription)
        private val cbDone: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bind(habit: Habit) {
            // İkon
            ivIcon.setImageResource(habit.iconResId)

            // İsim
            tvName.text = habit.name

            // Açıklama
            tvDescription.text = habit.description

            cbDone.isChecked = false
            cbDone.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    Toast.makeText(
                        itemView.context,
                        "${habit.name} tamamlandı!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

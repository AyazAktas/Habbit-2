package com.example.habbit.util

import com.example.habbit.data.local.entity.Habit
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

object  HabitUtils {
    fun shouldShowHabitToday(habit: Habit): Boolean{
        val today = Calendar.getInstance()
        return when(habit.repetitionType){
            "daily" -> {
                true
            }
            "weekly"->{
                val todayName= SimpleDateFormat("EEEE", Locale("tr")).format(today.time)
                habit.repetitionValue?.split(",")?.map { it.trim() }?.contains(todayName) == true
            }

            "custom"->{
                val diffInMillis = today.timeInMillis - habit.startDate.time
                val daysPassed = TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()

                when(habit.repetitionValue){
                    "2 günde bir" -> daysPassed % 2 == 0
                    "3 günde bir" -> daysPassed % 3 == 0
                    "5 günde bir" -> daysPassed % 5 == 0
                    "Haftada bir" -> daysPassed % 7 == 0
                    else -> false
                }
            }

            else -> {
                val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(today.time)
                val habitDateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(habit.startDate)
                todayStr == habitDateStr
            }
        }
    }
}
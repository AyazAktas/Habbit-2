package com.ayaz.habbit.util

import com.ayaz.habbit.data.local.entity.Habit
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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

    fun shouldShowHabitOnDate(habit: Habit, date: Date): Boolean {
        val cal = Calendar.getInstance().apply { time = date }
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1= Pazar, 2= Pazartesi, ...

        return when (habit.repetitionType) {
            "daily" -> true
            "weekly" -> {
                // örnek: repetitionValue = "Pazartesi,Çarşamba"
                habit.repetitionValue?.split(",")?.any { day ->
                    day.equals(getDayName(dayOfWeek), ignoreCase = true)
                } ?: false
            }
            "custom" -> {
                // custom: örneğin "3 günde bir"
                val diffDays = ((date.time - habit.startDate.time) / (1000 * 60 * 60 * 24)).toInt()
                val interval = habit.repetitionValue?.split(" ")?.firstOrNull()?.toIntOrNull() ?: 1
                diffDays % interval == 0
            }
            else -> false
        }
    }

    private fun getDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.MONDAY -> "Pazartesi"
            Calendar.TUESDAY -> "Salı"
            Calendar.WEDNESDAY -> "Çarşamba"
            Calendar.THURSDAY -> "Perşembe"
            Calendar.FRIDAY -> "Cuma"
            Calendar.SATURDAY -> "Cumartesi"
            Calendar.SUNDAY -> "Pazar"
            else -> ""
        }
    }
}
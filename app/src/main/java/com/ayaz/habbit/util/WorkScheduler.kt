package com.ayaz.habbit.util

import android.content.Context
import androidx.work.*
import com.ayaz.habbit.data.local.entity.Habit
import com.ayaz.habbit.notification.Worker.HabitReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit


object WorkScheduler {

    // 🔹 Günlük tekrar
    fun scheduleDaily(context: Context, habitData: Data) {
        val habitName = habitData.getString("habit_name") ?: "Alışkanlık"
        val hour = habitData.getInt("reminder_hour", 9)
        val minute = habitData.getInt("reminder_minute", 0)

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(now)) add(Calendar.DATE, 1)
        }

        val delay = target.timeInMillis - now.timeInMillis

        val data = workDataOf(
            "habit_name" to habitName,
            "reminder_hour" to hour,
            "reminder_minute" to minute,
            "repetition_type" to "daily"
        )

        val workRequest = OneTimeWorkRequestBuilder<HabitReminderWorker>()
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    // 🔹 Haftalık tekrar
    fun scheduleWeekly(context: Context, habit: Habit) {
        val days = habit.repetitionValue?.split(",") ?: return
        for (day in days) {
            val dayOfWeek = mapDayToCalendar(day.trim())
            val data = workDataOf(
                "habit_name" to habit.name,
                "reminder_hour" to 9,
                "reminder_minute" to 0,
                "repetition_type" to "weekly",
                "day_of_week" to dayOfWeek
            )
            scheduleForDayOfWeek(context, data, dayOfWeek)
        }
    }

    // 🔹 Custom tekrar
    fun scheduleCustom(context: Context, habit: Habit) {
        val intervalDays = when (habit.repetitionValue) {
            "2 günde bir" -> 2
            "3 günde bir" -> 3
            "5 günde bir" -> 5
            "Haftada bir" -> 7
            else -> 1
        }
        val data = workDataOf(
            "habit_name" to habit.name,
            "reminder_hour" to 9,
            "reminder_minute" to 0,
            "repetition_type" to "custom",
            "interval_days" to intervalDays
        )
        scheduleWithInterval(context, data, intervalDays)
    }

    // 🔧 Belirli gün için planlama
    fun scheduleForDayOfWeek(context: Context, habitData: Data, dayOfWeek: Int) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            set(Calendar.HOUR_OF_DAY, habitData.getInt("reminder_hour", 9))
            set(Calendar.MINUTE, habitData.getInt("reminder_minute", 0))
            set(Calendar.SECOND, 0)
            if (before(now)) add(Calendar.WEEK_OF_YEAR, 1)
        }

        val delay = target.timeInMillis - now.timeInMillis

        val workRequest = OneTimeWorkRequestBuilder<HabitReminderWorker>()
            .setInputData(habitData)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    // 🔧 Belirli gün aralığıyla planlama
    fun scheduleWithInterval(context: Context, habitData: Data, intervalDays: Int) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, habitData.getInt("reminder_hour", 9))
            set(Calendar.MINUTE, habitData.getInt("reminder_minute", 0))
            set(Calendar.SECOND, 0)
            if (before(now)) add(Calendar.DATE, intervalDays)
        }

        val delay = target.timeInMillis - now.timeInMillis

        val workRequest = OneTimeWorkRequestBuilder<HabitReminderWorker>()
            .setInputData(habitData)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    // 🔧 Gün ismini Calendar sabitine çevir
    private fun mapDayToCalendar(day: String): Int {
        return when (day.lowercase()) {
            "pzt", "pazartesi" -> Calendar.MONDAY
            "sal", "salı"      -> Calendar.TUESDAY
            "çar", "çarşamba"  -> Calendar.WEDNESDAY
            "per", "perşembe"  -> Calendar.THURSDAY
            "cum", "cuma"      -> Calendar.FRIDAY
            "cmt", "cumartesi" -> Calendar.SATURDAY
            "paz", "pazar"     -> Calendar.SUNDAY
            else -> Calendar.MONDAY
        }
    }
}
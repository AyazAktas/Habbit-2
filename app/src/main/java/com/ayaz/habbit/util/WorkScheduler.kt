package com.ayaz.habbit.util

import android.content.Context
import androidx.work.*
import com.ayaz.habbit.data.local.entity.Habit
import com.ayaz.habbit.notification.Worker.HabitReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit


object WorkScheduler {

    fun scheduleDaily(context: Context, habitData: Data, habitId: Int) {
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

        val workRequest = PeriodicWorkRequestBuilder<HabitReminderWorker>(1, TimeUnit.DAYS)
            .setInputData(habitData)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "habit_daily_$habitId",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun scheduleWeekly(context: Context, habit: Habit) {
        val days = habit.repetitionValue?.split(",") ?: return
        for (day in days) {
            val dayOfWeek = mapDayToCalendar(day.trim())

            val data = workDataOf(
                "habit_name" to habit.name
            )

            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, dayOfWeek)
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(now)) add(Calendar.WEEK_OF_YEAR, 1)
            }

            val delay = target.timeInMillis - now.timeInMillis

            val workRequest = PeriodicWorkRequestBuilder<HabitReminderWorker>(7, TimeUnit.DAYS)
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "habit_weekly_${habit.id}_$dayOfWeek",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    fun scheduleCustom(context: Context, habit: Habit) {
        val intervalDays = when (habit.repetitionValue) {
            "2 günde bir" -> 2
            "3 günde bir" -> 3
            "5 günde bir" -> 5
            "Haftada bir" -> 7
            else -> 1
        }

        val data = workDataOf("habit_name" to habit.name)

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(now)) add(Calendar.DATE, intervalDays)
        }

        val delay = target.timeInMillis - now.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<HabitReminderWorker>(intervalDays.toLong(), TimeUnit.DAYS)
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "habit_custom_${habit.id}_$intervalDays",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

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

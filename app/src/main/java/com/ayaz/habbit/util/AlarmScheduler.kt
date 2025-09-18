package com.ayaz.habbit.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import com.ayaz.habbit.data.local.entity.Habit
import com.ayaz.habbit.notification.NotificationReceiver

object AlarmScheduler {
    fun scheduleDaily(context: Context, habit: Habit) {
        val timeParts=habit.reminderTime?.split(":")
        val hour=timeParts?.getOrNull(0)?.toIntOrNull()?:9
        val minute=timeParts?.getOrNull(1)?.toIntOrNull()?:0

        val calendar= Calendar.getInstance().apply {
            timeInMillis= System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY,hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND,0)

            if (before(Calendar.getInstance())){
                add(Calendar.DATE,1)
            }
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("habit_id", habit.id)
            putExtra("habit_name", habit.name)
        }

        val pendingIntent= PendingIntent.getBroadcast(
            context,
            habit.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun scheduleWeekly(context: Context, habit: Habit) {
        val days = habit.repetitionValue?.split(",") ?: return
        val hour = 9
        val minute = 0

        for (day in days) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.DAY_OF_WEEK, mapDayToCalendar(day.trim()))
                if (before(Calendar.getInstance())) {
                    add(Calendar.WEEK_OF_YEAR, 1)
                }
            }

            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("habit_id", habit.id)
                putExtra("habit_name", habit.name)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                (habit.id + day.hashCode()),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY * 7,
                pendingIntent
            )
        }
    }

    fun scheduleCustom(context: Context, habit: Habit) {
        val daysInterval = when (habit.repetitionValue) {
            "2 günde bir" -> 2
            "3 günde bir" -> 3
            "5 günde bir" -> 5
            "Haftada bir" -> 7
            else -> 1
        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("habit_id", habit.id)
            putExtra("habit_name", habit.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habit.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * daysInterval,
            pendingIntent
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
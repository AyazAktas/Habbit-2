package com.ayaz.habbit.notification.Worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.ayaz.habbit.R
import com.ayaz.habbit.util.WorkScheduler

class HabitReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val habitName = inputData.getString("habit_name") ?: "Alışkanlık"
        val reminderHour = inputData.getInt("reminder_hour", 9)
        val reminderMinute = inputData.getInt("reminder_minute", 0)
        val repetitionType = inputData.getString("repetition_type") ?: "daily"
        val intervalDays = inputData.getInt("interval_days", 1) // custom için
        val dayOfWeek = inputData.getInt("day_of_week", -1)     // weekly için

        // 1️⃣ Bildirim oluştur
        val notification = NotificationCompat.Builder(context, "habbit_chanel")
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_round))
            .setContentTitle("Alışkanlık Hatırlatıcısı")
            .setContentText("$habitName için zaman geldi. Hadi hemen başla!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(
                System.currentTimeMillis().toInt(),
                notification
            )
        }

        // 2️⃣ Kendi tekrarını planla
        when (repetitionType) {
            "daily" -> {
                val nextData = workDataOf(
                    "habit_name" to habitName,
                    "reminder_hour" to reminderHour,
                    "reminder_minute" to reminderMinute,
                    "repetition_type" to "daily"
                )
                WorkScheduler.scheduleDaily(context, nextData)
            }

            "weekly" -> {
                if (dayOfWeek != -1) {
                    val nextData = workDataOf(
                        "habit_name" to habitName,
                        "reminder_hour" to reminderHour,
                        "reminder_minute" to reminderMinute,
                        "repetition_type" to "weekly",
                        "day_of_week" to dayOfWeek
                    )
                    WorkScheduler.scheduleForDayOfWeek(context, nextData, dayOfWeek)
                }
            }

            "custom" -> {
                val nextData = workDataOf(
                    "habit_name" to habitName,
                    "reminder_hour" to reminderHour,
                    "reminder_minute" to reminderMinute,
                    "repetition_type" to "custom",
                    "interval_days" to intervalDays
                )
                WorkScheduler.scheduleWithInterval(context, nextData, intervalDays)
            }
        }

        return Result.success()
    }
}
package com.ayaz.habbit.notification.Worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.util.Log
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
        val prefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean("notifications_enabled", true)
        if (!enabled) {
            Log.d("Worker", "Bildirimler kapalı, Worker hiçbir şey yapmayacak.")
            return Result.success()
        }
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

        return Result.success()
    }
}

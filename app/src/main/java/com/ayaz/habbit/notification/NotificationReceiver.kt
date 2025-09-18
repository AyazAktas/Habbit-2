package com.ayaz.habbit.notification

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ayaz.habbit.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val channelId="habbit_chanel"
        val habitName = intent.getStringExtra("habit_name") ?: "Alışkanlık"
        Log.d("Receiver", "Alarm tetiklendi! Bildirim gönderiliyor...")
        val notification= NotificationCompat.Builder(context,channelId)
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
            with(NotificationManagerCompat.from(context)) {
                notify(System.currentTimeMillis().toInt(), notification)
            }
        }

    }
}

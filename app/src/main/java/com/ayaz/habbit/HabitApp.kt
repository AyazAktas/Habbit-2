package com.ayaz.habbit

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build

class HabitApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId="habbit_chanel"
            val channelName="Habbit Hatırlatıcıları"
            val channelDescription="Alışkanlıklar için günlük/haftalık bildirimler"

            val importance= NotificationManager.IMPORTANCE_HIGH
            val channel= NotificationChannel(channelId,channelName,importance).apply {
                description=channelDescription
                enableVibration(true)
                vibrationPattern=longArrayOf(0,500,250,500)
                enableLights(true)
                lightColor= Color.GREEN
            }

            val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
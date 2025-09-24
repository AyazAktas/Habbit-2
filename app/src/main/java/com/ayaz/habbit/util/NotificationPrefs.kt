package com.ayaz.habbit.util

import android.content.Context

class NotificationPrefs(context: Context) {
    private val prefs=context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun setNotificationsEnabled(enabled: Boolean){
        prefs.edit().putBoolean("notifications_enabled",enabled).apply()
    }

    fun areNotificationsEnabled(): Boolean{
        return prefs.getBoolean("notifications_enabled",true)
    }
}
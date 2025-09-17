package com.example.habbit.util

import android.content.Context

object Prefs {
    private const val FILE = "habbit_prefs"
    private const val KEY_ONBOARD_DONE = "onboard_done"

    fun isOnboardDone(ctx: Context): Boolean =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getBoolean(KEY_ONBOARD_DONE, false)

    fun setOnboardDone(ctx: Context, done: Boolean) {
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ONBOARD_DONE, done).apply()
    }
}
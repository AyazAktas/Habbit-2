package com.example.habbit.ui.habbit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.habbit.data.prefs.Prefs

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val next = if (Prefs.isOnboardDone(this)) MainActivity::class.java
        else OnboardingActivity::class.java

        startActivity(Intent(this, next))
        finish() // bu activity geri yığında kalmasın
    }
}

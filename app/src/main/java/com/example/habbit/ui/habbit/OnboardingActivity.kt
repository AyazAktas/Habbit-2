package com.example.habbit.ui.habbit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.habbit.R
import com.example.habbit.util.Prefs

class OnboardingActivity : AppCompatActivity() {

    private lateinit var vp: ViewPager2
    private lateinit var btnNext: Button
    private lateinit var dots: List<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        vp = findViewById(R.id.vpOnboard)
        btnNext = findViewById(R.id.btnNext)
        dots = listOf(
            findViewById(R.id.dot1),
            findViewById(R.id.dot2),
            findViewById(R.id.dot3)
        )

        val pages = listOf(
            R.layout.item_onboard_welcome,
            R.layout.item_onboard_feature
            // İstersen üçüncü sayfa layout'u ekleyebilirsin
        )

        vp.adapter = OnboardAdapter(pages)
        vp.isUserInputEnabled = true

        updateUi(0)

        vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) = updateUi(position)
        })

        btnNext.setOnClickListener {
            val last = (vp.adapter?.itemCount ?: 1) - 1
            val pos = vp.currentItem
            if (pos < last) {
                vp.currentItem = pos + 1
            } else {
                // Finish → MainActivity
                Prefs.setOnboardDone(this, true)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun updateUi(position: Int) {
        // indicator görünümü
        dots.forEachIndexed { index, view ->
            if (index == position) {
                // aktif: uzun bar
                val params = view.layoutParams
                params.width = dp(28)
                params.height = dp(10)
                view.layoutParams = params
                view.setBackgroundResource(R.drawable.indicator_active)
            } else {
                val params = view.layoutParams
                params.width = dp(10)
                params.height = dp(10)
                view.layoutParams = params
                view.setBackgroundResource(R.drawable.indicator_inactive)
            }
        }

        // buton yazısı
        btnNext.text = when (position) {
            0 -> "Get Started"
            (vp.adapter?.itemCount ?: 1) - 1 -> "Finish"
            else -> "Next"
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

}

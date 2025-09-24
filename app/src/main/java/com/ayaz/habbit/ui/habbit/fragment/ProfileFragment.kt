package com.ayaz.habbit.ui.habbit.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ayaz.habbit.R
import com.ayaz.habbit.data.local.AppDatabase
import com.ayaz.habbit.data.repository.HabitCompletionRepository
import com.ayaz.habbit.data.repository.HabitRepository
import com.ayaz.habbit.ui.habbit.ViewModel.HabitCompletionViewModel
import com.ayaz.habbit.ui.habbit.ViewModel.HabitViewModel
import com.ayaz.habbit.ui.habbit.ViewModelFactory.HabitCompletionViewModelFactory
import com.ayaz.habbit.ui.habbit.ViewModelFactory.HabitViewModelFactory
import com.ayaz.habbit.util.NotificationPrefs
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var habitViewModel: HabitViewModel

    private lateinit var notificationPrefs: NotificationPrefs
    private lateinit var completionViewModel: HabitCompletionViewModel
    private lateinit var switchNotification: Switch

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getInstance(requireContext())
        habitViewModel = ViewModelProvider(
            this,
            HabitViewModelFactory(HabitRepository(db.habitDao()))
        )[HabitViewModel::class.java]

        completionViewModel = ViewModelProvider(
            this,
            HabitCompletionViewModelFactory(HabitCompletionRepository(db.habitCompletionDao()))
        )[HabitCompletionViewModel::class.java]

        val cardDeleteHabit = view.findViewById<MaterialCardView>(R.id.cardDeleteHabit)
        val cardDeleteAll = view.findViewById<MaterialCardView>(R.id.cardDeleteAll)
        switchNotification = view.findViewById(R.id.switchNotification)
        notificationPrefs = NotificationPrefs(requireContext())
        cardDeleteHabit.setOnClickListener {
            showDeleteHabitDialog()
        }

        cardDeleteAll.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Tüm Veriyi Sil")
                .setMessage("Tüm alışkanlıklar ve ilerlemeler silinecek. Emin misiniz?")
                .setPositiveButton("Evet") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        val db = AppDatabase.getInstance(requireContext())
                        db.clearAllTables()

                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Tüm veriler silindi", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("İptal", null)
                .show()
        }

        switchNotification.isChecked = notificationPrefs.areNotificationsEnabled()

        // ✅ Toggle değişince kaydet
        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            notificationPrefs.setNotificationsEnabled(isChecked)
            Log.d("Toggle", "Bildirim tercihi değişti: $isChecked")

            val message = if (isChecked) "Bildirimler Açıldı" else "Bildirimler Kapatıldı"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteHabitDialog() {
        val dao = AppDatabase.getInstance(requireContext()).habitDao()
        lifecycleScope.launch {
            val habits = dao.getAllHabitsOnce()
            val habitNames = habits.map { it.name }.toTypedArray()
            AlertDialog.Builder(requireContext())
                .setTitle("Alışkanlık Sil")
                .setItems(habitNames) { _, which ->
                    val selectedHabit = habits[which]
                    lifecycleScope.launch {
                        habitViewModel.deleteHabit(selectedHabit)
                        Toast.makeText(requireContext(), "${selectedHabit.name} silindi", Toast.LENGTH_SHORT).show()
                    }
                }
                .show()
        }
    }
}

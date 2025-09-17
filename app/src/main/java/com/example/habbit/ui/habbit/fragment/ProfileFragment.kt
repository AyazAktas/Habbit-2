package com.example.habbit.ui.habbit.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.habbit.R
import com.example.habbit.data.local.AppDatabase
import com.example.habbit.data.repository.HabitCompletionRepository
import com.example.habbit.data.repository.HabitRepository
import com.example.habbit.ui.habbit.ViewModel.HabitCompletionViewModel
import com.example.habbit.ui.habbit.ViewModel.HabitViewModel
import com.example.habbit.ui.habbit.ViewModelFactory.HabitCompletionViewModelFactory
import com.example.habbit.ui.habbit.ViewModelFactory.HabitViewModelFactory
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var habitViewModel: HabitViewModel
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


        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), "Bildirimler Açıldı", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Bildirimler Kapatıldı", Toast.LENGTH_SHORT).show()
            }
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

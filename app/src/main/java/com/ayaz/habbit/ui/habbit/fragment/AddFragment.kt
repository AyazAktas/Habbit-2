package com.ayaz.habbit.ui.habbit.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.work.workDataOf
import com.ayaz.habbit.R
import com.ayaz.habbit.data.local.AppDatabase
import com.ayaz.habbit.data.local.entity.Habit
import com.ayaz.habbit.data.repository.HabitRepository
import com.ayaz.habbit.databinding.FragmentAddBinding
import com.ayaz.habbit.ui.habbit.ViewModel.HabitViewModel
import com.ayaz.habbit.ui.habbit.ViewModelFactory.HabitViewModelFactory
import com.ayaz.habbit.util.WorkScheduler
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.Date
import java.util.Locale

class AddFragment : Fragment(R.layout.fragment_add) {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HabitViewModel
    private var selectedDate: Long = System.currentTimeMillis()
    private var selectedIconId: Int? = null

    private var selectedTime: String? = null

    private var repetitionType: String? = null

    private var repetitionValue: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddBinding.bind(view)
        setupIconSelection()

        val today = Calendar.getInstance().time
        val formattedToday = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(today)
        binding.etStartDate.setText(formattedToday)
        selectedDate = Calendar.getInstance().timeInMillis

        binding.rgRepetition.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbDaily -> {
                    repetitionType = "daily"
                    showTimePicker()
                }
                R.id.rbSpecificDays -> {
                    repetitionType = "weekly"
                    showDaysSelectionDialog()
                }
                R.id.rbCustomPeriod -> {
                    repetitionType = "custom"
                    showCustomPeriodDialog()
                }
            }
        }

        binding.etStartDate.setOnClickListener {
            showDatePicker()
        }

        binding.icCalendar.setOnClickListener {
            showDatePicker()
        }

        val dao = AppDatabase.getInstance(requireContext()).habitDao()
        val repository = HabitRepository(dao)
        val factory = HabitViewModelFactory(repository)

        viewModel= ViewModelProvider(this,factory)[HabitViewModel::class.java]

        binding.btnAddHabit.setOnClickListener {
            saveHabit()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupIconSelection() {
        val icons = listOf(binding.iconLotus, binding.iconBooks, binding.iconNotes, binding.iconMuscle)

        icons.forEach { iconButton ->
            iconButton.setOnClickListener {
                // Önce tüm ikonların arka planını sıfırla
                icons.forEach { it.setBackgroundResource(R.drawable.bg_circle_border) }

                // Seçilen ikona "aktif" arka plan ver
                iconButton.setBackgroundResource(R.drawable.bg_circle_selected)

                // Seçilen ikonun drawable id’sini sakla
                selectedIconId = when (iconButton.id) {
                    R.id.iconLotus -> R.drawable.meditation
                    R.id.iconBooks -> R.drawable.books
                    R.id.iconNotes -> R.drawable.notebook
                    R.id.iconMuscle -> R.drawable.muscle
                    else -> null
                }
            }
        }
    }

    private fun showDaysSelectionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_days_selection, null)
        val chipGroup = dialogView.findViewById<ChipGroup>(R.id.chipGroupDays)

        AlertDialog.Builder(requireContext())
            .setTitle("Gün Seç")
            .setView(dialogView)
            .setPositiveButton("Tamam") { _, _ ->
                val selectedDays = mutableListOf<String>()
                for (i in 0 until chipGroup.childCount) {
                    val chip = chipGroup.getChildAt(i) as Chip
                    if (chip.isChecked) {
                        selectedDays.add(chip.text.toString())
                    }
                }
                repetitionValue = selectedDays.joinToString(",")
                Log.d("Habit", "Seçilen günler: $selectedDays")
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun showCustomPeriodDialog() {
        val periods = arrayOf( "2 günde bir", "3 günde bir", "5 günde bir","Haftada bir")
        var selected = 0

        AlertDialog.Builder(requireContext())
            .setTitle("Döngü Seç")
            .setSingleChoiceItems(periods, selected) { _, which ->
                selected = which
            }
            .setPositiveButton("Tamam") { _, _ ->
                val chosenPeriod = periods[selected]
                Log.d("Habit", "Seçilen döngü: $chosenPeriod")
                repetitionValue = chosenPeriod
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, y, m, d ->
            // Kullanıcı tarih seçtiğinde burası çalışır
            val chosenCalendar = Calendar.getInstance()
            chosenCalendar.set(y, m, d)

            selectedDate = chosenCalendar.timeInMillis // timestamp kaydet
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(chosenCalendar.time)

            binding.etStartDate.setText(formattedDate) // EditText içine yaz
        }, year, month, day)

        datePicker.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(requireContext(), { _, h, m ->
            selectedTime = String.format("%02d:%02d", h, m)
            repetitionValue = selectedTime
        }, hour, minute, true)
        timePicker.show()
    }

    private fun saveHabit() {
        val name = binding.etName.text.toString()
        val description = binding.etDescription.text.toString()
        val icon = selectedIconId ?: R.drawable.app_logo

        val habit = Habit(
            name = name,
            description = description,
            iconResId = icon,
            repetitionType = repetitionType ?: "daily",
            repetitionValue = repetitionValue,
            startDate = Date(selectedDate),
            reminderTime = selectedTime
        )

        viewModel.addHabit(habit) { id ->
            val habitWithId = habit.copy(id = id.toInt())

            when (habitWithId.repetitionType) {
                "daily" -> {
                    val timeParts = habitWithId.reminderTime?.split(":")
                    val hour = timeParts?.getOrNull(0)?.toIntOrNull() ?: 9
                    val minute = timeParts?.getOrNull(1)?.toIntOrNull() ?: 0

                    val data = workDataOf(
                        "habit_name" to habitWithId.name,
                        "reminder_hour" to hour,
                        "reminder_minute" to minute
                    )
                    WorkScheduler.scheduleDaily(requireContext(), data, habitWithId.id)
                }
                "weekly" -> {
                    WorkScheduler.scheduleWeekly(requireContext(), habitWithId)
                }
                "custom" -> {
                    WorkScheduler.scheduleCustom(requireContext(), habitWithId)
                }
            }

            Log.d("Habit", "Kaydedildi ve WorkManager ile planlandı: $habitWithId")
        }

        Toast.makeText(requireContext(), "Alışkanlık ${habit.name} eklendi!", Toast.LENGTH_SHORT).show()
    }
}

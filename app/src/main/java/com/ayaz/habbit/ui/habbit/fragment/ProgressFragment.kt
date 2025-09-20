package com.ayaz.habbit.ui.habbit.fragment

import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.CalendarView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayaz.habbit.R
import com.ayaz.habbit.data.local.AppDatabase
import com.ayaz.habbit.data.repository.HabitCompletionRepository
import com.ayaz.habbit.data.repository.HabitRepository
import com.ayaz.habbit.ui.habbit.Adapter.HabitAdapter
import com.ayaz.habbit.ui.habbit.ViewModel.HabitCompletionViewModel
import com.ayaz.habbit.ui.habbit.ViewModel.HabitViewModel
import com.ayaz.habbit.ui.habbit.ViewModelFactory.HabitCompletionViewModelFactory
import com.ayaz.habbit.ui.habbit.ViewModelFactory.HabitViewModelFactory
import com.ayaz.habbit.util.HabitUtils
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Date

class ProgressFragment : Fragment(R.layout.fragment_progress) {

    private lateinit var calendarView: CalendarView
    private lateinit var rvHabits: RecyclerView
    private lateinit var progressDaily: ProgressBar
    private lateinit var tvProgressPercent: TextView

    private lateinit var habitAdapter: HabitAdapter
    private lateinit var habitViewModel: HabitViewModel
    private lateinit var completionViewModel: HabitCompletionViewModel

    private var selectedDate: Date = Date()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendarView)
        rvHabits = view.findViewById(R.id.rvHabits)
        progressDaily = view.findViewById(R.id.progressDaily)
        tvProgressPercent = view.findViewById(R.id.tvProgressPercent)

        habitAdapter = HabitAdapter { habit, isChecked ->
            completionViewModel.toggleCompletion(habit.id, selectedDate, isChecked)
        }
        rvHabits.layoutManager = LinearLayoutManager(requireContext())
        rvHabits.adapter = habitAdapter

        val db = AppDatabase.getInstance(requireContext())
        habitViewModel = ViewModelProvider(
            this,
            HabitViewModelFactory(HabitRepository(db.habitDao()))
        )[HabitViewModel::class.java]

        completionViewModel = ViewModelProvider(
            this,
            HabitCompletionViewModelFactory(HabitCompletionRepository(db.habitCompletionDao()))
        )[HabitCompletionViewModel::class.java]

        selectedDate = getStartOfDay(Date())
        calendarView.date = selectedDate.time
        loadHabitsForDate(selectedDate)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            selectedDate = cal.time
            loadHabitsForDate(selectedDate)
        }
    }

    private fun getStartOfDay(date: Date): Date {
        val cal = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.time
    }

    private fun loadHabitsForDate(date: Date) {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                habitViewModel.allHabits,
                completionViewModel.getCompletionsByDate(date)
            ) { habits, completions ->
                val filteredHabits = habits.filter { HabitUtils.shouldShowHabitOnDate(it, date) }
                val completed = completions.filter { it.isCompleted }.map { it.habitId }.toSet()
                Pair(filteredHabits, completed)
            }.collect { (habits, completed) ->
                habitAdapter.setHabits(habits, completed)
                updateProgress(habits.size, completed.size)
            }
        }
    }

    private fun updateProgress(total: Int, done: Int) {
        val percent = if (total > 0) (done * 100 / total) else 0
        progressDaily.progress = percent
        tvProgressPercent.text = "$percent%"
    }
}
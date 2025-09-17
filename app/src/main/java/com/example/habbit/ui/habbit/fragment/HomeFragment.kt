package com.example.habbit.ui.habbit.fragment

import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habbit.R
import com.example.habbit.data.local.AppDatabase
import com.example.habbit.data.local.entity.Habit
import com.example.habbit.data.repository.HabitCompletionRepository
import com.example.habbit.data.repository.HabitRepository
import com.example.habbit.ui.habbit.Adapter.HabitAdapter
import com.example.habbit.ui.habbit.ViewModel.HabitCompletionViewModel
import com.example.habbit.ui.habbit.ViewModel.HabitViewModel
import com.example.habbit.ui.habbit.ViewModelFactory.HabitCompletionViewModelFactory
import com.example.habbit.ui.habbit.ViewModelFactory.HabitViewModelFactory
import com.example.habbit.util.HabitUtils
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Date

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var adapter: HabitAdapter
    private lateinit var viewModel: HabitViewModel
    private lateinit var completionViewModel: HabitCompletionViewModel
    private lateinit var progressDaily: ProgressBar
    private lateinit var tvProgressPercent: TextView

    private var todayHabits: List<Habit> = emptyList()
    private val completedHabits = mutableSetOf<Int>()
    private var completedIds: Set<Int> = emptySet()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDaily = view.findViewById(R.id.progressDaily)
        tvProgressPercent = view.findViewById(R.id.tvProgressPercent)

        val rvHabits = view.findViewById<RecyclerView>(R.id.rvHabits)
        adapter = HabitAdapter { habit, isChecked ->
            val today = getTodayDate()
            completionViewModel.toggleCompletion(habit.id, today, isChecked)
        }


        rvHabits.adapter = adapter
        rvHabits.layoutManager = LinearLayoutManager(requireContext())

        val dao = AppDatabase.getInstance(requireContext()).habitDao()
        val repository = HabitRepository(dao)
        val factory = HabitViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HabitViewModel::class.java]

        val completionDao = AppDatabase.getInstance(requireContext()).habitCompletionDao()
        val completionRepo = HabitCompletionRepository(completionDao)
        val completionFactory = HabitCompletionViewModelFactory(completionRepo)
        completionViewModel = ViewModelProvider(this, completionFactory)[HabitCompletionViewModel::class.java]

        val today = getTodayDate()

        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                viewModel.allHabits,
                completionViewModel.getCompletionsByDate(today)
            ) { habits, completions ->
                todayHabits = habits.filter { HabitUtils.shouldShowHabitToday(it) }
                completedIds = completions.filter { it.isCompleted }.map { it.habitId }.toSet()
            }.collect {
                adapter.setHabits(todayHabits, completedIds)
                updateProgress()
            }
        }

    }

    private fun updateProgress() {
        val total = todayHabits.size
        val done = completedIds.size   // 🔹 artık sadece DB’den gelen set
        val percent = if (total > 0) (done * 100 / total) else 0

        progressDaily.progress = percent
        tvProgressPercent.text = "$percent%"
    }


    private fun getTodayDate(): Date {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.time
    }
}
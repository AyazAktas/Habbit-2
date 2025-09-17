package com.example.habbit.ui.habbit.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habbit.data.local.entity.HabitCompletion
import com.example.habbit.data.repository.HabitCompletionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Date

class HabitCompletionViewModel(private val repository: HabitCompletionRepository): ViewModel() {

    fun getCompletionsByDate(date: Date): Flow<List<HabitCompletion>>{
        return repository.getCompletionsByDate(date)
    }

    fun toggleCompletion(habitId: Int, date: Date, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.upsertCompletion(habitId, date, isCompleted)
        }
    }
}
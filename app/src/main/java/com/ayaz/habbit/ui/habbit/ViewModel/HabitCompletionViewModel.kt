package com.ayaz.habbit.ui.habbit.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaz.habbit.data.local.entity.HabitCompletion
import com.ayaz.habbit.data.repository.HabitCompletionRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import java.util.Date

class HabitCompletionViewModel(
    private val repository: HabitCompletionRepository
) : ViewModel() {
    fun getCompletionsByDate(date: Date): Flow<List<HabitCompletion>> {
        return repository.getCompletionsByDate(date)
    }
    fun toggleCompletion(habitId: Int, date: Date, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleCompletion(habitId, date, isCompleted)
        }
    }
}
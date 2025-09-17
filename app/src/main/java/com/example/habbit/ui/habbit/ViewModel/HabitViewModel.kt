package com.example.habbit.ui.habbit.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habbit.data.local.entity.Habit
import com.example.habbit.data.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {
    val allHabits: Flow<List<Habit>> = repository.getAllHabits()

    fun addHabit(habit: Habit, onInserted: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertHabit(habit)
            onInserted(id)
        }
    }

    fun deleteHabit(habit: Habit){
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun updateHabit(habit: Habit){
        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }
}
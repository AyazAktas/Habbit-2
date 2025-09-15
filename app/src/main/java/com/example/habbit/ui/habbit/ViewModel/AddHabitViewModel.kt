package com.example.habbit.ui.habbit.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habbit.data.local.entity.Habit
import com.example.habbit.data.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AddHabitViewModel(private val repository: HabitRepository) : ViewModel() {
    val allHabits: Flow<List<Habit>> = repository.getAllHabits()

    fun addHabit(habit: Habit){
        viewModelScope.launch{
            repository.insertHabit(habit)
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
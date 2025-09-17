package com.example.habbit.ui.habbit.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habbit.data.repository.HabitCompletionRepository
import com.example.habbit.data.repository.HabitRepository
import com.example.habbit.ui.habbit.ViewModel.HabitCompletionViewModel
import com.example.habbit.ui.habbit.ViewModel.HabitViewModel

class HabitCompletionViewModelFactory(
    private val repository: HabitCompletionRepository
): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>):T{
        if (modelClass.isAssignableFrom(HabitCompletionViewModel::class.java)){
            return HabitCompletionViewModel(repository) as T
        }
        throw IllegalArgumentException("Bilinmeyen ViewModel Sınıfı")
    }
}
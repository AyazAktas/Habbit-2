package com.example.habbit.ui.habbit.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habbit.data.repository.HabitRepository
import com.example.habbit.ui.habbit.ViewModel.AddHabitViewModel

class AddHabitViewModelFactory(
    private val repository: HabitRepository
): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>):T{
        if (modelClass.isAssignableFrom(AddHabitViewModel::class.java)){
            return AddHabitViewModel(repository) as T
        }
        throw IllegalArgumentException("Bilinmeyen ViewModel Sınıfı")
    }
}
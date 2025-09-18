package com.ayaz.habbit.ui.habbit.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayaz.habbit.data.repository.HabitRepository
import com.ayaz.habbit.ui.habbit.ViewModel.HabitViewModel

class HabitViewModelFactory(
    private val repository: HabitRepository
): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>):T{
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)){
            return HabitViewModel(repository) as T
        }
        throw IllegalArgumentException("Bilinmeyen ViewModel Sınıfı")
    }
}
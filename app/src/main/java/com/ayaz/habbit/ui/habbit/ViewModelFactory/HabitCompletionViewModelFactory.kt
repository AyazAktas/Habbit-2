package com.ayaz.habbit.ui.habbit.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayaz.habbit.data.repository.HabitCompletionRepository
import com.ayaz.habbit.ui.habbit.ViewModel.HabitCompletionViewModel

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
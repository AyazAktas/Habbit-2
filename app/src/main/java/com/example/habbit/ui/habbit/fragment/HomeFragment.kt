package com.example.habbit.ui.habbit.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habbit.R
import com.example.habbit.data.local.AppDatabase
import com.example.habbit.data.repository.HabitRepository
import com.example.habbit.ui.habbit.Adapter.HabitAdapter
import com.example.habbit.ui.habbit.ViewModel.HabitViewModel
import com.example.habbit.ui.habbit.ViewModelFactory.HabitViewModelFactory
import com.example.habbit.util.HabitUtils
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home){
    private lateinit var adapter: HabitAdapter
    private lateinit var viewModel: HabitViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvHabits=view.findViewById<RecyclerView>(R.id.rvHabits)
        adapter= HabitAdapter()
        rvHabits.adapter=adapter
        rvHabits.layoutManager= LinearLayoutManager(requireContext())

        val dao= AppDatabase.getInstance(requireContext()).habitDao()
        val repository = HabitRepository(dao)
        val factory= HabitViewModelFactory(repository)
        viewModel= ViewModelProvider(this,factory)[HabitViewModel::class.java]

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.allHabits.collect { habits ->
                val todayHabits=habits.filter{ HabitUtils.shouldShowHabitToday(it) }
                adapter.setHabits(todayHabits)
            }
        }
    }
}
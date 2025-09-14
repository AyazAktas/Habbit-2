package com.example.habbit.ui.habbit.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.habbit.R
import com.example.habbit.databinding.FragmentAddBinding

class AddFragment : Fragment(R.layout.fragment_add) {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private var selectedIconId: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddBinding.bind(view)
        setupIconSelection()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupIconSelection() {
        val icons = listOf(binding.iconLotus, binding.iconBooks, binding.iconNotes, binding.iconMuscle)

        icons.forEach { iconButton ->
            iconButton.setOnClickListener {
                // Önce tüm ikonların arka planını sıfırla
                icons.forEach { it.setBackgroundResource(R.drawable.bg_circle_border) }

                // Seçilen ikona "aktif" arka plan ver
                iconButton.setBackgroundResource(R.drawable.bg_circle_selected)

                // Seçilen ikonun drawable id’sini sakla
                selectedIconId = when (iconButton.id) {
                    R.id.iconLotus -> R.drawable.meditation
                    R.id.iconBooks -> R.drawable.books
                    R.id.iconNotes -> R.drawable.notebook
                    R.id.iconMuscle -> R.drawable.muscle
                    else -> null
                }
            }
        }
    }
}

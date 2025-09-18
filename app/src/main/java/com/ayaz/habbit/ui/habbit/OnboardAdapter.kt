package com.ayaz.habbit.ui.habbit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class OnboardAdapter(
    private val layouts: List<Int>
) : RecyclerView.Adapter<OnboardAdapter.VH>() {

    inner class VH(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) { /* No binding needed */ }

    override fun getItemCount() = layouts.size

    override fun getItemViewType(position: Int): Int = layouts[position]
}
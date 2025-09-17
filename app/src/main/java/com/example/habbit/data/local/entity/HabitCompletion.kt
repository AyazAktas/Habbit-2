package com.example.habbit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity("habit_completion")
data class HabitCompletion (
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    val habitId:Int,
    val date: Date,
    val isCompleted: Boolean
)
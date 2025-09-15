package com.example.habbit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "habit")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val name: String,
    val description: String,
    val iconResId:Int,
    val repetitionType: String,
    val repetitionValue: String?,
    val startDate: Date,
    val reminderTime: String?
)
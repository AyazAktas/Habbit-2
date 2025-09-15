package com.example.habbit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.habbit.data.local.dao.HabitDao
import com.example.habbit.data.local.entity.Habit


@Database(entities = [Habit::class], version = 1, exportSchema = true)
abstract class AppDatabase: RoomDatabase() {
    abstract fun habitDao(): HabitDao
}
package com.example.habbit.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.habbit.data.local.entity.Habit
import kotlinx.coroutines.flow.Flow

interface HabitDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Query("SELECT * FROM HABIT ORDER BY startDate DESC")
    fun getAllHabits(): Flow<List<Habit>>
}
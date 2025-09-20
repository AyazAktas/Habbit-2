package com.ayaz.habbit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ayaz.habbit.data.local.entity.HabitCompletion
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface HabitCompletionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletion)

    @Query("""
        UPDATE habit_completion 
        SET isCompleted = :isCompleted 
        WHERE habitId = :habitId AND date BETWEEN :startOfDay AND :endOfDay
    """)
    suspend fun updateCompletion(
        habitId: Int,
        startOfDay: Date,
        endOfDay: Date,
        isCompleted: Boolean
    )

    @Query("""
        SELECT * FROM habit_completion 
        WHERE date BETWEEN :startOfDay AND :endOfDay
    """)
    fun getCompletionsByDate(
        startOfDay: Date,
        endOfDay: Date
    ): Flow<List<HabitCompletion>>

    @Query("""
        SELECT * FROM habit_completion 
        WHERE habitId = :habitId AND date BETWEEN :startOfDay AND :endOfDay 
        LIMIT 1
    """)
    suspend fun getCompletionForHabit(
        habitId: Int,
        startOfDay: Date,
        endOfDay: Date
    ): HabitCompletion?
}
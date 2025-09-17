package com.example.habbit.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.habbit.data.local.entity.HabitCompletion
import com.example.habbit.ui.habbit.Adapter.HabitAdapter
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface HabitCompletionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletion)

    @Query("UPDATE habit_completion SET isCompleted = :isCompleted WHERE habitId = :habitId AND date = :date")
    suspend fun updateCompletion(habitId: Int, date: Date, isCompleted: Boolean)

    @Query("SELECT * FROM habit_completion where date= :date")
    fun getCompletionsByDate(date: Date): Flow<List<HabitCompletion>>

    @Query("select * from habit_completion where habitId= :habitId and date= :date limit 1")
    suspend fun getCompletionForHabit(habitId:Int,date: Date): HabitCompletion?

}
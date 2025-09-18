package com.ayaz.habbit.data.repository

import com.ayaz.habbit.data.local.dao.HabitCompletionDao
import com.ayaz.habbit.data.local.entity.HabitCompletion
import kotlinx.coroutines.flow.Flow
import java.util.Date

class HabitCompletionRepository(private val dao: HabitCompletionDao) {

    fun getCompletionsByDate(date: Date):Flow<List<HabitCompletion>>{
        return  dao.getCompletionsByDate(date)
    }

    suspend fun upsertCompletion(habitId:Int, date: Date, isCompleted: Boolean){
        val existing = dao.getCompletionForHabit(habitId, date)
        if (existing == null) {
            val completion = HabitCompletion(
                habitId = habitId,
                date = date,
                isCompleted = isCompleted
            )
            dao.insertCompletion(completion)
        } else {
            dao.updateCompletion(habitId, date, isCompleted)
        }
    }


    suspend fun getCompletionForHabit(habitId:Int,date: Date): HabitCompletion?{
        return dao.getCompletionForHabit(habitId,date)
    }
}
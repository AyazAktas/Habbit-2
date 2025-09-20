package com.ayaz.habbit.data.repository

import com.ayaz.habbit.data.local.dao.HabitCompletionDao
import com.ayaz.habbit.data.local.entity.HabitCompletion
import kotlinx.coroutines.flow.Flow
import java.util.Date

class HabitCompletionRepository(private val dao: HabitCompletionDao) {

    suspend fun toggleCompletion(habitId: Int, date: Date, isCompleted: Boolean) {
        val (startOfDay, endOfDay) = getDayBounds(date)
        val existing = dao.getCompletionForHabit(habitId, startOfDay, endOfDay)

        if (existing == null) {
            dao.insertCompletion(
                HabitCompletion(
                    habitId = habitId,
                    date = startOfDay, // 🔹 normalize ederek kaydediyoruz
                    isCompleted = isCompleted
                )
            )
        } else {
            dao.updateCompletion(habitId, startOfDay, endOfDay, isCompleted)
        }
    }

    fun getCompletionsByDate(date: Date): Flow<List<HabitCompletion>> {
        val (startOfDay, endOfDay) = getDayBounds(date)
        return dao.getCompletionsByDate(startOfDay, endOfDay)
    }

    private fun getDayBounds(date: Date): Pair<Date, Date> {
        val cal = java.util.Calendar.getInstance().apply {
            time = date
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val startOfDay = cal.time

        cal.set(java.util.Calendar.HOUR_OF_DAY, 23)
        cal.set(java.util.Calendar.MINUTE, 59)
        cal.set(java.util.Calendar.SECOND, 59)
        cal.set(java.util.Calendar.MILLISECOND, 999)
        val endOfDay = cal.time

        return Pair(startOfDay, endOfDay)
    }
}

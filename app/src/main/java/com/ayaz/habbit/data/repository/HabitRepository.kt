package com.ayaz.habbit.data.repository

import com.ayaz.habbit.data.local.dao.HabitDao
import com.ayaz.habbit.data.local.entity.Habit

class HabitRepository(private val habitDao: HabitDao) {
    fun getAllHabits()=habitDao.getAllHabits()
    suspend fun insertHabit(habit: Habit): Long=habitDao.insertHabit(habit)
    suspend fun updateHabit(habit: Habit)=habitDao.updateHabit(habit)
    suspend fun deleteHabit(habit: Habit)=habitDao.deleteHabit(habit)
}
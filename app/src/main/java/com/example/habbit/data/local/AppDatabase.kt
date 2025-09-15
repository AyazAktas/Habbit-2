package com.example.habbit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.habbit.data.local.dao.HabitDao
import com.example.habbit.data.local.entity.Habit


@Database(entities = [Habit::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun habitDao(): HabitDao
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase?=null

        fun getInstance(context: Context): AppDatabase{
            return INSTANCE?:synchronized(this){
                val instance= Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habbit_db"
                ).build()
                INSTANCE=instance
                instance
            }
        }
    }
}
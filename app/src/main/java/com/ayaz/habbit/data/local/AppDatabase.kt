package com.ayaz.habbit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ayaz.habbit.data.local.dao.HabitCompletionDao
import com.ayaz.habbit.data.local.dao.HabitDao
import com.ayaz.habbit.data.local.entity.Habit
import com.ayaz.habbit.data.local.entity.HabitCompletion

@Database(
    entities = [Habit::class, HabitCompletion::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS habit_completion (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        habitId INTEGER NOT NULL,
                        date INTEGER NOT NULL,
                        isCompleted INTEGER NOT NULL
                    )
                    """
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS habit_completion_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        habitId INTEGER NOT NULL,
                        date INTEGER NOT NULL,
                        isCompleted INTEGER NOT NULL,
                        FOREIGN KEY(habitId) REFERENCES habit(id) ON DELETE CASCADE
                    )
                    """
                )
                database.execSQL(
                    """
                    INSERT INTO habit_completion_new (id, habitId, date, isCompleted)
                    SELECT id, habitId, date, isCompleted FROM habit_completion
                    """
                )
                database.execSQL("DROP TABLE habit_completion")
                database.execSQL("ALTER TABLE habit_completion_new RENAME TO habit_completion")
            }
        }

        // 🔥 3 → 4 (UNIQUE index ekle)
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_habit_completion_habitId_date ON habit_completion(habitId, date)"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habbit_db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            db.execSQL("PRAGMA foreign_keys=ON;")
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

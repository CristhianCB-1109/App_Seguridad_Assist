package com.example.guardia

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RegistroAlumno::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun registroAlumnoDao(): RegistroAlumnoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "seguridad_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

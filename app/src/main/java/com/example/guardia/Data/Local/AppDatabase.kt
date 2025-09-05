package com.example.guardia.Data.Local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.guardia.Data.Local.entities.RegistroAlumno
import com.example.guardia.Data.Local.entities.RegistroInvitado

@Database(entities = [RegistroInvitado::class, RegistroAlumno::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun registroAlumnoDao(): RegistroAlumnoDao
    abstract fun registroInvitadoDao(): RegistroInvitadoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "seguridad_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
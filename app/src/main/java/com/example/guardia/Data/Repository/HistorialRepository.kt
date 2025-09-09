package com.example.guardia.Data.Repository

import com.example.guardia.Data.Local.AppDatabase
import com.example.guardia.Data.Local.entities.RegistroAlumno

class HistorialRepository(private val db: AppDatabase) {

    suspend fun obtenerHistorial(): List<RegistroAlumno> {
        return db.registroAlumnoDao().obtenerHistorial()
    }
}

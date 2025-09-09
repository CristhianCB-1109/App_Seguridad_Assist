package com.example.guardia.Data.Repository

import com.example.guardia.Data.Local.AppDatabase
import com.example.guardia.Data.Local.entities.RegistroInvitado

class HistorialInvitadoRepository(private val db: AppDatabase) {

    suspend fun obtenerHistorial(): List<RegistroInvitado> {
        return db.registroInvitadoDao().obtenerTodos()
    }
}

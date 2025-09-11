package com.example.guardia.Data.Repository

import com.example.guardia.Data.Local.AppDatabase
import com.example.guardia.Data.Local.entities.RegistroAlumno
import com.example.guardia.Data.Local.entities.RegistroInvitado
import com.example.guardia.model.Alumno

class RegistroInvitadoRepository(private val db: AppDatabase) {

    private val registroInvitadoDao = db.registroInvitadoDao()

    suspend fun insertarInvitado(invitado: RegistroInvitado) {
        registroInvitadoDao.insertarInvitado(invitado)
    }

    suspend fun obtenerHistorial(): List<RegistroInvitado> {
        return registroInvitadoDao.obtenerTodos()
    }

}

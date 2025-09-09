package com.example.guardia.Data.Repository

import com.example.guardia.Data.Local.AppDatabase
import com.example.guardia.Data.Local.entities.RegistroInvitado

class RegistroInvitadoRepository(private val db: AppDatabase) {

    suspend fun insertarInvitado(invitado: RegistroInvitado) {
        db.registroInvitadoDao().insertarInvitado(invitado)
    }
}

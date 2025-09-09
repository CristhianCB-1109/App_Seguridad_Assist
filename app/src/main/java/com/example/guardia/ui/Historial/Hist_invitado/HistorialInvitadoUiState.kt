package com.example.guardia.ui.Historial

import com.example.guardia.Data.Local.entities.RegistroInvitado

data class HistorialInvitadoUiState(
    val lista: List<RegistroInvitado> = emptyList(),
    val mensaje: String? = null
)

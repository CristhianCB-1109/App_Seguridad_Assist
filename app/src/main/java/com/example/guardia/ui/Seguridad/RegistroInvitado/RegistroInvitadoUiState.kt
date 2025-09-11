package com.example.guardia.ui.Seguridad.RegistroInvitado

data class RegistroInvitadoUiState(
    val nombre: String = "",
    val dni: String = "",
    val numero: String = "",
    val mensaje: String? = null,
    val fechaRegistro: String? = null
)

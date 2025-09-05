package com.example.guardia.model

data class LoginResult(
    val errorMessage: String? = null,
    val rol: String? = null,
    val id: String? = null,
    val nombre: String? = null,
    val codigoEstudiante: String? = null,
    val carrera: String? = null,
    val foto: String? = null,
    val telefono: String? = null,
    val dni: String? = null
)
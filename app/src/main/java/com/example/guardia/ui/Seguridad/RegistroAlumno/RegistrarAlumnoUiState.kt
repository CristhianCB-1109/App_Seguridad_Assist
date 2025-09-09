package com.example.guardia.ui.Seguridad.RegistroAlumno

import com.example.guardia.model.Alumno

data class RegistrarAlumnoUiState(
    val alumno: Alumno? = null,
    val qrValido: Boolean = false,
    val mensaje: String? = null
)

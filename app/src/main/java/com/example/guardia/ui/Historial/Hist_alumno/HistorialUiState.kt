package com.example.guardia.ui.Historial.Hist_alumno

import com.example.guardia.Data.Local.entities.RegistroAlumno

data class HistorialUiState(
    val lista: List<RegistroAlumno> = emptyList(),
    val mensaje: String? = null
)

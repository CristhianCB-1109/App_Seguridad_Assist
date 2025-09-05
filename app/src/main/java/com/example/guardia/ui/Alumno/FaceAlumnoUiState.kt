package com.example.guardia.ui.Alumno

import android.graphics.Bitmap

data class FaceAlumnoUiState(
    val nombre: String = "",
    val codigo: String = "",
    val carrera: String = "",
    val dni: String = "",
    val telefono: String = "",
    val foto: String = "",
    val qrBitmap: Bitmap? = null,
    val isLoading: Boolean = true,   // 👈 Aquí ya existe
    val error: String? = null
)

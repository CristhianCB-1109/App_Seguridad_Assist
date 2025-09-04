package com.example.guardia

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registro_alumno")
data class RegistroAlumno(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigo: String,
    val nombre: String,
    val carrera: String,
    val fechaentrada: String,
    val fechasalida: String? = null
)

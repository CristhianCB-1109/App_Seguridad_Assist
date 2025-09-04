package com.example.guardia

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RegistroAlumnoDao {

    @Insert
    suspend fun insertarRegistro(registro: RegistroAlumno)

    @Query("SELECT * FROM registro_alumno ORDER BY id DESC")
    suspend fun obtenerHistorial(): List<RegistroAlumno>

    @Query("SELECT * FROM registro_alumno WHERE codigo = :codigo ORDER BY id DESC LIMIT 1")
    suspend fun obtenerUltimoRegistro(codigo: String): RegistroAlumno?

    @Query("UPDATE registro_alumno SET fechasalida = :horaSalida WHERE id = :id")
    suspend fun registrarSalida(id: Int, horaSalida: String)

}

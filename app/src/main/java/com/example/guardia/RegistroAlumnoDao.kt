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

}

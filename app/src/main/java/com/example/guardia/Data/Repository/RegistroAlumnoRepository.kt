package com.example.guardia.Data.Repository

import com.example.guardia.Data.Local.AppDatabase
import com.example.guardia.Data.Local.entities.RegistroAlumno
import com.example.guardia.model.Alumno

class RegistroAlumnoRepository(private val db: AppDatabase) {

    private val dao = db.registroAlumnoDao()

    suspend fun obtenerUltimoRegistro(codigo: String): RegistroAlumno? {
        return dao.obtenerUltimoRegistro(codigo)
    }

    suspend fun registrarEntrada(alumno: Alumno) {
        val registro = RegistroAlumno(
            codigo = alumno.codigo,
            nombre = alumno.nombre,
            carrera = alumno.carrera,
            fechaentrada = System.currentTimeMillis().toString()
        )
        dao.insertarRegistro(registro)
    }

    suspend fun registrarSalida(id: Int, hora: String) {
        dao.registrarSalida(id, hora)
    }
}

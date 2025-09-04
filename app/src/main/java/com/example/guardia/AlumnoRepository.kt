package com.example.guardia

object AlumnoRepository {
    suspend fun getAlumno(id: String): AlumnoResponse {
        return try {
            val response = ClienteRetrofit.api.getAlumno(id)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                getMockAlumno(id)
            }
        } catch (e: Exception) {
            getMockAlumno(id)
        }
    }

    private fun getMockAlumno(id: String): AlumnoResponse {
        return AlumnoResponse(
            id = "A2025001",
            nombre = "Juan Pérez",
            carrera = "Ingeniería de Software",
            foto = "foto",
            codigo_estudiante = "A2025001",
            dni = "75894612",
            telefono = "952010187"
        )
    }
}

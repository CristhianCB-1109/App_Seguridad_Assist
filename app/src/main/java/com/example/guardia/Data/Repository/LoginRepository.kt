package com.example.guardia.Data.Repository

import com.example.guardia.Data.Remote.ApiService
import com.example.guardia.Data.Remote.LoginRequest
import com.example.guardia.model.LoginResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://j-c-g.apis-s.site/") // 🔑 tu API base
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    suspend fun login(email: String, password: String): LoginResult {
        return try {
            val response = apiService.login(LoginRequest(email, password))

            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!
                LoginResult(
                    rol = data.rol,
                    id = data.id,
                    nombre = data.nombre,
                    codigoEstudiante = data.codigo_estudiante,
                    carrera = data.carrera,
                    foto = data.foto,
                    telefono = data.telefono,
                    dni = data.dni
                )
            } else {
                LoginResult(errorMessage = response.body()?.message ?: "Credenciales incorrectas")
            }
        } catch (e: Exception) {
            mockLogin(email, password)
        }
    }

    // Mock en caso de error
    private fun mockLogin(email: String, password: String): LoginResult {
        return when (email.lowercase()) {
            "alumno@ejemplo.com" -> LoginResult(
                rol = "alumno",
                id = "A2025001",
                nombre = "Juan Pérez",
                codigoEstudiante = "A2025001",
                carrera = "Ingeniería de Software",
                foto = "https://picsum.photos/200",
                telefono = "987654321",
                dni = "12345678"
            )
            "seguridad@ejemplo.com" -> LoginResult(
                rol = "seguridad",
                id = "S2025001",
                nombre = "Carlos Ramírez",
                dni = "87654321",
                foto = "https://picsum.photos/201"
            )
            else -> LoginResult(errorMessage = "Usuario o contraseña incorrectos")
        }
    }
}

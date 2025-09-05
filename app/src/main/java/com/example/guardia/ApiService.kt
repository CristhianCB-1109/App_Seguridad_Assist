package com.example.guardia

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response

// Definimos los endpoints de la API
interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body requestBody: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body requestBody: RegisterRequest): Response<RegisterResponse>

    @GET("api/alumno/{id}")
    suspend fun getAlumno(@Path("id") id: String): Response<AlumnoResponse>
}
// datos alumno
data class AlumnoResponse(
    val id: String,
    val nombre: String,
    val carrera: String,
    val foto: String,
    val codigo_estudiante: String,
    val dni: String,
    val telefono: String
)
// Definimos los objetos de datos (DTOs) para las peticiones
data class LoginRequest(
    val email: String, // ¡CORREGIDO A 'email'!
    val
    contrasena: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val rol: String?,
    val id: String?,
    val nombre: String?,
    val codigo_estudiante: String?,
    val carrera: String?,
    val foto: String?,
    val telefono: String?,
    val dni: String?
)
data class RegisterRequest(
    val email: String,
    val contrasena: String,
    val rol: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String
)
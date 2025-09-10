package com.example.guardia.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

// Definiciones de datos para la API
data class ApiResponse(val success: Boolean, val message: String)
data class LoginRequest(val email: String? = null, val contrasena: String? = null, val clave_acceso: String? = null)
data class LoginResponse(val success: Boolean, val token: String, val message: String, val user: User)
data class User(
    val id: String,
    val email: String,
    val rol: String,
    val nombre: String?,
    val codigo_estudiante: String?,
    val carrera: String?,
    val dni: String?,
    val telefono: String?
)
data class RegisterRequest(
    val email: String,
    val contrasena: String,
    val rol: String,
    val nombre: String?,
    val codigo_estudiante: String?,
    val carrera: String?,
    val dni: String?,
    val telefono: String?
)
data class RegistroAlumnoRequest(
    val tipo: String = "alumno",
    val id_usuario: String,
    val nombre: String,
    val dni_o_codigo: String,
    val carrera: String
)
data class RegistroInvitadoRequest(
    val tipo: String = "invitado",
    val nombre: String,
    val dni_o_codigo: String
)
data class HistorialResponse(
    val success: Boolean,
    val historial: List<Registro>
)

data class Registro(
    val id_registro: Int,
    val tipo: String,
    val id_usuario: Int?,
    val id_invitado: Int?,
    val nombre: String,
    val dni_o_codigo: String,
    val carrera: String?,
    val hora_entrada: String,
    val hora_salida: String?
)

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<ApiResponse>

    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/records/alumno")
    suspend fun registrarAlumno(@Body registroRequest: RegistroAlumnoRequest): Response<ApiResponse>

    @POST("api/records/invitado")
    suspend fun registrarInvitado(@Body registroInvitadoRequest: RegistroInvitadoRequest): Response<ApiResponse>

    @GET("api/records/historial/alumnos")
    suspend fun getHistorialAlumnos(): Response<HistorialResponse>

    @GET("api/records/historial/invitados")
    suspend fun getHistorialInvitados(): Response<HistorialResponse>

    // Este endpoint permite verificar si un alumno ya está dentro.
    // Aunque tu API original no lo tenía, es necesario para que el botón de "Marcar salida" funcione correctamente.
    @GET("api/records/historial/alumnos")
    suspend fun obtenerUltimoRegistro(@Query("dni_o_codigo") dni_o_codigo: String): Response<HistorialResponse>
}
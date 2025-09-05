package com.example.guardia.Data.Repository

import com.example.guardia.Data.Remote.ApiService
import com.example.guardia.Data.Remote.RegisterRequest
import com.example.guardia.model.RegisterResult
import com.example.guardia.Data.Remote.ClienteRetrofit

class RegisterRepository(private val api: ApiService = ClienteRetrofit.api) {

    suspend fun registerUser(email: String, password: String, rol: String): RegisterResult {
        return try {
            val response = api.register(RegisterRequest(email, password, rol))
            if (response.isSuccessful && response.body()?.success == true) {
                RegisterResult(success = true, message = response.body()?.message)
            } else {
                RegisterResult(success = false, message = response.body()?.message ?: "Error al registrarse")
            }
        } catch (e: Exception) {
            RegisterResult(success = false, message = "Error de red: ${e.message}")
        }
    }
}

package com.example.guardia

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.guardia.api.ApiClient
import com.example.guardia.api.ApiService
import com.example.guardia.api.RegistroInvitadoRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class RegistroInvitadoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDni: EditText
    private lateinit var etNumero: EditText
    private lateinit var btnGuardar: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_invitado)

        // Inicializar vistas
        etNombre = findViewById(R.id.RegistroFullName)
        etDni = findViewById(R.id.RegistroDni)
        etNumero = findViewById(R.id.RegistroNumero)
        btnGuardar = findViewById(R.id.RegistroInvitado)

        // Inicializar el servicio de la API
        apiService = ApiClient.apiService

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val dni = etDni.text.toString().trim()
            val numero = etNumero.text.toString().trim()

            if (nombre.isNotBlank() && dni.isNotBlank() && numero.isNotBlank()) {
                // Iniciar una corrutina para la llamada a la red
                lifecycleScope.launch {
                    try {
                        val registroRequest = RegistroInvitadoRequest(
                            nombre = nombre,
                            dni_o_codigo = dni
                        )

                        val response = withContext(Dispatchers.IO) {
                            apiService.registrarInvitado(registroRequest)
                        }

                        if (response.isSuccessful) {
                            val apiResponse = response.body()
                            if (apiResponse?.success == true) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@RegistroInvitadoActivity, apiResponse.message, Toast.LENGTH_SHORT).show()
                                    // Limpiar los campos después de un registro exitoso
                                    etNombre.text.clear()
                                    etDni.text.clear()
                                    etNumero.text.clear()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    val errorMessage = apiResponse?.message ?: "Error desconocido en la API"
                                    Toast.makeText(this@RegistroInvitadoActivity, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@RegistroInvitadoActivity, "Error en la solicitud: ${response.code()}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: IOException) {
                        // Error de conexión a internet
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegistroInvitadoActivity, "Error de red. Verifica tu conexión a internet.", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: HttpException) {
                        // Errores HTTP como 404, 500, etc.
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegistroInvitadoActivity, "Error en el servidor: ${e.code()}", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        // Otros errores inesperados
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegistroInvitadoActivity, "Ocurrió un error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
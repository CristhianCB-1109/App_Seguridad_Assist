package com.example.guardia

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardia.ui.theme.GuardiaTheme
import com.example.guardia.ApiService
import com.example.guardia.LoginRequest
import com.example.guardia.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuardiaTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    LoginScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "El correo y la contraseña no pueden estar vacíos", Toast.LENGTH_LONG).show()
                    return@Button
                }

                isLoading = true
                coroutineScope.launch {
                    var result: LoginResult

                    try {
                        if (email.lowercase() == "alumno@ejemplo.com" || email.lowercase() == "seguridad@ejemplo.com") {
                            // Forzamos datos mock para la prueba
                            result = mockLogin(email, password)
                        } else {
                            // Intentar API
                            val errorMessage = loginUser(email, password)
                            if (errorMessage == null) {

                                val rol = when {
                                    email.contains("alumno", ignoreCase = true) -> "alumno"
                                    email.contains("seguridad", ignoreCase = true) -> "seguridad"
                                    else -> "otro"
                                }
                                result = LoginResult(rol = rol, id = "API_USER")
                            } else {
                                result = LoginResult(errorMessage = errorMessage)
                            }
                        }
                    } catch (e: Exception) {
                        result = mockLogin(email, password)
                    }

                    isLoading = false

                    if (result.errorMessage != null) {
                        Toast.makeText(
                            context,
                            "Error: ${result.errorMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Inicio de sesión exitoso",
                            Toast.LENGTH_SHORT
                        ).show()

                        when (result.rol) {
                            "alumno" -> {
                                context.startActivity(
                                    Intent(context, FaceAlumnoActivity::class.java).apply {
                                        putExtra("id", result.id)
                                        putExtra("nombre", "Juan Pérez") // mock
                                        putExtra("carrera", "Ingeniería de Software") // mock
                                        putExtra("fotoUrl", "https://picsum.photos/200") // mock
                                    }
                                )
                            }
                            "seguridad" -> {
                                context.startActivity(Intent(context, SeguridadActivity::class.java))
                            }
                            else -> {
                                context.startActivity(Intent(context, GestionActivity::class.java))
                            }
                        }
                    }
                }
            },
                    /* datos con api (Gandy)
                    val errorMessage = loginUser(
                        email = email,
                        contrasena = password
                    )
                    isLoading = false

                    if (errorMessage == null) {
                        Toast.makeText(context, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show()
                        // Redirigir a la nueva actividad
                        context.startActivity(Intent(context, GestionActivity::class.java))
                    } else {
                        Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            },*/
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Ingresar")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        TextButton(onClick = {
            context.startActivity(Intent(context, RegisterActivity::class.java))
        }) {
            Text(" Regístrate aquí")
        }
    }
}

@Preview(showBackground = true, name = "Vista previa de inicio de sesión")
@Composable
fun LoginScreenPreview() {
    GuardiaTheme {
        LoginScreen()
    }
}
//Login con Mock

suspend fun mockLogin(email: String, password: String): LoginResult {
    return when (email.lowercase()) {
        "alumno@ejemplo.com" -> LoginResult(rol = "alumno", id = "A2025001")
        "seguridad@ejemplo.com" -> LoginResult(rol = "seguridad", id = "S2025001")
        else -> LoginResult(errorMessage = "Usuario o contraseña incorrectos")
    }
}

data class LoginResult(
    val errorMessage: String? = null,
    val rol: String? = null,
    val id: String? = null
)

//Login con API
suspend fun loginUser(email: String, contrasena: String): String? {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://j-c-g.apis-s.site/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    return try {
        val response = apiService.login(LoginRequest(email, contrasena))
        if (response.isSuccessful && response.body()?.success == true) {
            null //
        } else {
            response.body()?.message ?: "Credenciales incorrectas"
        }
    } catch (e: Exception) {
        throw e
    }
}
/*
// Función para iniciar sesión usando la API (Gandy)
suspend fun loginUser(email: String, contrasena: String): String? {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://j-c-g.apis-s.site/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    return try {
        val response = apiService.login(LoginRequest(email = email, contrasena = contrasena))
        if (response.isSuccessful && response.body()?.success == true) {
            null
        } else {
            response.body()?.message ?: "Credenciales incorrectas"
        }
    } catch (e: Exception) {
        "Error de red: ${e.message}"
    }
} */
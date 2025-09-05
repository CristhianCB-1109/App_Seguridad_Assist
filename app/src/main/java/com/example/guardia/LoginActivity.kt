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
import androidx.compose.material3.MaterialTheme

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
    var accessCode by remember { mutableStateOf("") }
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

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = accessCode,
            onValueChange = { accessCode = it },
            label = { Text("Clave de Acceso (Solo Seguridad)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (email.isBlank() && accessCode.isBlank()) {
                    Toast.makeText(context, "Ingresa un correo y contraseña, o una clave de acceso", Toast.LENGTH_LONG).show()
                    return@Button
                }

                isLoading = true
                coroutineScope.launch {
                    val result = login(email, password, accessCode)
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
                                        putExtra("id", result.id ?: "")
                                        putExtra("nombre", result.nombre ?: "")
                                        putExtra("carrera", result.carrera ?: "")
                                        putExtra("fotoUrl", result.foto ?: "")
                                        putExtra("telefono", result.telefono ?: "")
                                        putExtra("dni", result.dni ?: "")
                                        putExtra("isMockData", result.isMockData)
                                    }
                                )
                            }
                            "seguridad" -> {
                                context.startActivity(
                                    Intent(context, SeguridadActivity::class.java).apply {
                                        putExtra("id", result.id ?: "")
                                        putExtra("nombre", result.nombre ?: "")
                                        putExtra("dni", result.dni ?: "")
                                        putExtra("fotoUrl", result.foto ?: "")
                                        putExtra("isMockData", result.isMockData)
                                    }
                                )
                            }
                            else -> {
                                context.startActivity(
                                    Intent(context, GestionActivity::class.java)
                                )
                            }
                        }
                    }
                }
            },
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
            Text("Regístrate aquí")
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

// 🔑 CAMBIO 1: Hacemos los campos opcionales para evitar errores de null
data class LoginRequest(
    val email: String?,
    val contrasena: String?,
    val clave_acceso: String?
)

data class LoginResult(
    val errorMessage: String? = null,
    val rol: String? = null,
    val id: String? = null,
    val nombre: String? = null,
    val codigoEstudiante: String? = null,
    val carrera: String? = null,
    val foto: String? = null,
    val telefono: String? = null,
    val dni: String? = null,
    val isMockData: Boolean = false
)

// Login con API y Mock
suspend fun login(email: String, password: String, accessCode: String): LoginResult {
    return try {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://j-c-g.apis-s.site/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val requestBody = if (accessCode.isNotBlank()) {
            LoginRequest(null, null, accessCode)
        } else {
            LoginRequest(email, password, null)
        }

        val response = apiService.login(requestBody)

        if (response.isSuccessful && response.body()?.success == true) {
            val data = response.body()!!

            return LoginResult(
                rol = data.rol,
                id = data.id,
                nombre = data.nombre,
                codigoEstudiante = data.codigo_estudiante,
                carrera = data.carrera,
                foto = data.foto,
                telefono = data.telefono,
                dni = data.dni,
                isMockData = false
            )
        } else {
            LoginResult(errorMessage = response.body()?.message ?: "Credenciales incorrectas")
        }
    } catch (e: Exception) {
        return mockLogin(email, password, accessCode)
    }
}

suspend fun mockLogin(email: String, password: String, accessCode: String): LoginResult {
    return when {
        accessCode == "9988" -> LoginResult(
            rol = "seguridad",
            id = "S2025001",
            nombre = "Carlos Seguridad",
            dni = "87654321",
            foto = "https://picsum.photos/201",
            isMockData = true
        )
        email.lowercase() == "alumno@ejemplo.com" -> LoginResult(
            rol = "alumno",
            id = "A2025001",
            nombre = "Juan Pérez",
            codigoEstudiante = "A2025001",
            carrera = "Ingeniería de Software",
            foto = "https://picsum.photos/200",
            telefono = "987654321",
            dni = "12345678",
            isMockData = true
        )
        email.lowercase() == "alumno2@ejemplo.com" -> LoginResult(
            rol = "alumno",
            id = "e111111",
            nombre = "roberto",
            codigoEstudiante = "e234234",
            carrera = "Ingeniería de Software",
            foto = "https://picsum.photos/200",
            telefono = "924876666",
            dni = "12323433",
            isMockData = true
        )
        email.lowercase() == "seguridad@ejemplo.com" -> LoginResult(
            rol = "seguridad",
            id = "S2025001",
            nombre = "Carlos Ramírez",
            dni = "87654321",
            foto = "https://picsum.photos/201",
            isMockData = true
        )
        else -> LoginResult(errorMessage = "Usuario o contraseña incorrectos")
    }
}
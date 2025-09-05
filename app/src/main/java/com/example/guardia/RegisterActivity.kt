package com.example.guardia

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardia.ui.theme.GuardiaTheme
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuardiaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegisterScreen(onNavigateBack = { finish() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onNavigateBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.seg),
                contentDescription = "Logo de la aplicación",
                modifier = Modifier.padding(bottom = 32.dp)
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
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                        Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (password != confirmPassword) {
                        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(context, "Formato de correo inválido", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (password.length < 6) {
                        Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    isLoading = true
                    coroutineScope.launch {
                        // 🔑 ¡Corrección aquí! Pasando null para los nuevos parámetros opcionales
                        val errorMessage = registerUser(
                            correo = email,
                            contrasena = password,
                            rol = "alumno",
                            nombre = null,
                            codigo_estudiante = null,
                            carrera = null,
                            dni = null,
                            telefono = null,
                            clave_acceso = null
                        )
                        isLoading = false

                        if (errorMessage == null) {
                            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_LONG).show()
                            onNavigateBack()
                        } else {
                            Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_LONG).show()
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
                    Text("Registrarse")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Vista previa de registro")
@Composable
fun RegisterScreenPreview() {
    GuardiaTheme {
        RegisterScreen(onNavigateBack = {})
    }
}

// 🔑 ¡Corrección aquí! La función ahora acepta todos los nuevos parámetros
suspend fun registerUser(
    correo: String,
    contrasena: String,
    rol: String,
    nombre: String?,
    codigo_estudiante: String?,
    carrera: String?,
    dni: String?,
    telefono: String?,
    clave_acceso: String?
): String? {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://j-c-g.apis-s.site/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    return try {
        val response = apiService.register(
            RegisterRequest(
                email = correo,
                contrasena = contrasena,
                rol = rol,
                nombre = nombre,
                codigo_estudiante = codigo_estudiante,
                carrera = carrera,
                dni = dni,
                telefono = telefono,
                clave_acceso = clave_acceso
            )
        )
        if (response.isSuccessful && response.body()?.success == true) {
            null
        } else {
            response.body()?.message ?: "Error al registrarse"
        }
    } catch (e: Exception) {
        "Error de red: ${e.message}"
    }
}
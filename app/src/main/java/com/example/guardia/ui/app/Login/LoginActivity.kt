package com.example.guardia.ui.app.Login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.guardia.ui.Alumno.FaceAlumnoActivity
import com.example.guardia.ui.Gestion.GestionActivity
import com.example.guardia.ui.Register.RegisterActivity
import com.example.guardia.ui.Seguridad.SeguridadActivity
import com.example.guardia.ui.theme.GuardiaTheme

class LoginActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuardiaTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    LoginScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))

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
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Ingresar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            context.startActivity(Intent(context, RegisterActivity::class.java))
        }) {
            Text("Regístrate aquí")
        }
    }

    // 🔥 Reacciones a cambios en el estado
    LaunchedEffect(uiState) {
        uiState?.let { result ->
            if (result.errorMessage != null) {
                Toast.makeText(context, "Error: ${result.errorMessage}", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                when (result.rol) {
                    "alumno" -> {
                        context.startActivity(Intent(context, FaceAlumnoActivity::class.java).apply {
                            putExtra("id", result.id)
                            putExtra("nombre", result.nombre)
                            putExtra("carrera", result.carrera)
                            putExtra("fotoUrl", result.foto)
                            putExtra("telefono", result.telefono)
                            putExtra("dni", result.dni)
                        })
                    }
                    "seguridad" -> {
                        context.startActivity(Intent(context, SeguridadActivity::class.java).apply {
                            putExtra("id", result.id)
                            putExtra("nombre", result.nombre)
                            putExtra("dni", result.dni)
                            putExtra("fotoUrl", result.foto)
                        })
                    }
                    else -> {
                        context.startActivity(Intent(context, GestionActivity::class.java))
                    }
                }
            }
        }
    }
}

package com.example.guardia.ui.Gestion

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.guardia.ui.Alumno.FaceAlumnoActivity
import com.example.guardia.ui.Main.MainActivity
import com.example.guardia.ui.theme.GuardiaTheme

class GestionActivity : ComponentActivity() {
    //Funcion para cerrar sesion (Limpiar_todo)
    companion object {
        fun cerrarSesion(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)

            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val alumnoId = "A2025001"
        val alumnoNombre = "Juan Pérez"
        val alumnoCarrera = "Ingeniería de Software"
        val alumnoFotoUrl = "https://tu-api.com/fotos/juan.png"

        //valores con API
        //val alumno = apiResponse.alumno
        //val alumnoId = alumno.id
        //val alumnoNombre = alumno.nombre
        //val alumnoCarrera = alumno.carrera
        //val alumnoFotoUrl = alumno.fotoUrl

        setContent {
            GuardiaTheme {
                Surface(
                    modifier = Modifier.Companion.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Aquí puedes añadir la interfaz de usuario de tu pantalla principal.
                    Column(
                        modifier = Modifier.Companion
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Companion.CenterHorizontally
                    ) {
                        Text(
                            text = "¡Bienvenido, has iniciado sesión correctamente!",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Button(
                            onClick = {
                                // Enviar datos al activity del alumno
                                val intent = Intent(
                                    this@GestionActivity,
                                    FaceAlumnoActivity::class.java
                                ).apply {
                                    putExtra("id", alumnoId)
                                    putExtra("nombre", alumnoNombre)
                                    putExtra("carrera", alumnoCarrera)
                                    putExtra("fotoUrl", alumnoFotoUrl)
                                }
                                startActivity(intent)
                            },
                            modifier = Modifier.Companion.padding(top = 16.dp)
                        ) {
                            Text("Ver Carnet Alumno")
                        }

                        // Botón de ejemplo para cerrar sesión
                        Button(onClick = {
                            cerrarSesion(this@GestionActivity)
                        }, modifier = Modifier.Companion.padding(top = 16.dp)) {
                            Text("Cerrar Sesión")
                        }
                    }
                }
            }
        }
    }
}
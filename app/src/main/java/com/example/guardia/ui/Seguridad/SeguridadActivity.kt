package com.example.guardia.ui.Seguridad

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.guardia.ui.Gestion.GestionActivity
import com.example.guardia.ui.Historial.HistorialActivity
import com.example.guardia.ui.Historial.HistorialInvitado
import com.example.guardia.R
import com.example.guardia.ui.Seguridad.RegistroInvitadoActivity

class SeguridadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguridad)

        val nombre = intent.getStringExtra("nombre") ?: "Usuario Seguridad"

        // titulo
        supportActionBar?.title = "Bienvenido, $nombre"
        Toast.makeText(this, "Bienvenido $nombre", Toast.LENGTH_LONG).apply {
            setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 200)
            show()
        }

        //Registrar Alumno
        val btnRegistrarAlumno = findViewById<Button>(R.id.btnRegistrarAlumno)
        btnRegistrarAlumno.setOnClickListener {
            startActivity(Intent(this, RegistrarAlumnoActivity::class.java))
        }

        //Historial Alumnos
        val btnHistorial = findViewById<Button>(R.id.btnHistorial)
        btnHistorial.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }

        //Registrar Invitado
        val btnInvitado = findViewById<Button>(R.id.btnInvitado)
        btnInvitado.setOnClickListener {
            startActivity(Intent(this, RegistroInvitadoActivity::class.java))
        }

        //Historial Invitados
        val btnHistorialInvitado = findViewById<Button>(R.id.btnHistorialInvitado)
        btnHistorialInvitado.setOnClickListener {
            startActivity(Intent(this, HistorialInvitado::class.java))
        }

        //Cerrar Sesión
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesionSeguridad)
        btnCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }

        //Botón atrás cerrar sesión
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mostrarDialogoCerrarSesion()
            }
        })
    }
    // Función cerrar sesión
    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                GestionActivity.Companion.cerrarSesion(this)
            }
            .setNegativeButton("No", null)
            .show()
    }
}
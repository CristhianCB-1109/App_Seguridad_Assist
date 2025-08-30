package com.example.guardia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.OnBackPressedCallback

class SeguridadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguridad)


        supportActionBar?.title = "Panel de Seguridad"

        val btnRegistrarAlumno = findViewById<Button>(R.id.btnRegistrarAlumno)
        btnRegistrarAlumno.setOnClickListener {
            startActivity(Intent(this, RegistrarAlumnoActivity::class.java))
        }

        val btnHistorial = findViewById<Button>(R.id.btnHistorial)
        btnHistorial.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }
        val btnInvitado = findViewById<Button>(R.id.btnInvitado)
        btnInvitado.setOnClickListener {
            startActivity(Intent(this, RegistroInvitadoActivity::class.java))
        }

        val btnHistorialInvitado = findViewById<Button>(R.id.btnHistorialInvitado)
        btnHistorialInvitado.setOnClickListener {
            startActivity(Intent(this, HistorialInvitado::class.java))
        }

        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesionSeguridad)
        btnCerrarSesion.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que deseas cerrar sesión?")
                .setPositiveButton("Sí") { _, _ ->
                    GestionActivity.cerrarSesion(this)
                }
                .setNegativeButton("No", null)
                .show()
        }

        val toast = Toast.makeText(this, "Bienvenido Seguridad", Toast.LENGTH_LONG)
        toast.setGravity(android.view.Gravity.TOP or android.view.Gravity.CENTER_HORIZONTAL, 0, 200)
        toast.show()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // GestionActivity.cerrarSesion(this@SeguridadActivity) boton de atras cierra sesion
            }
        })
    }
}


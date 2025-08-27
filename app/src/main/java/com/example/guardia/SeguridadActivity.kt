package com.example.guardia

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SeguridadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguridad)

        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenidaSeguridad)
        tvBienvenida.text = "Inicio de sesión como Seguridad exitoso "

        Toast.makeText(this, "Bienvenido Seguridad ", Toast.LENGTH_LONG).show()

        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesionSeguridad)
        btnCerrarSesion.setOnClickListener {
            GestionActivity.cerrarSesion(this)
        }
    }
}
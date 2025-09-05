package com.example.guardia.ui.Seguridad

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.guardia.Data.Local.AppDatabase
import com.example.guardia.Data.Local.entities.RegistroInvitado
import com.example.guardia.R
import kotlinx.coroutines.launch

class RegistroInvitadoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDni: EditText
    private lateinit var etNumero: EditText
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_invitado)

        etNombre = findViewById(R.id.RegistroFullName)
        etDni = findViewById(R.id.RegistroDni)
        etNumero = findViewById(R.id.RegistroNumero)
        btnGuardar = findViewById(R.id.RegistroInvitado)

        val db = AppDatabase.Companion.getDatabase(this)
        val dao = db.registroInvitadoDao()

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val dni = etDni.text.toString()
            val numero = etNumero.text.toString()

            if (nombre.isNotBlank() && dni.isNotBlank() && numero.isNotBlank()) {
                val invitado = RegistroInvitado(nombre = nombre, dni = dni, numero = numero)
                lifecycleScope.launch {
                    dao.insertarInvitado(invitado)
                    runOnUiThread {
                        Toast.makeText(this@RegistroInvitadoActivity, "Invitado guardado", Toast.LENGTH_SHORT).show()
                        etNombre.text.clear()
                        etDni.text.clear()
                        etNumero.text.clear()
                    }
                }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
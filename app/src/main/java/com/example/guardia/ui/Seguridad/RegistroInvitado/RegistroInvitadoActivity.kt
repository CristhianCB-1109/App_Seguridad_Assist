package com.example.guardia.ui.Seguridad.RegistroInvitado

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.guardia.Data.Local.AppDatabase
import com.example.guardia.Data.Repository.RegistroInvitadoRepository
import com.example.guardia.R
import kotlinx.coroutines.launch

class RegistroInvitadoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDni: EditText
    private lateinit var etNumero: EditText
    private lateinit var btnGuardar: Button

    private lateinit var viewModel: RegistroInvitadoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_invitado)

        etNombre = findViewById(R.id.RegistroFullName)
        etDni = findViewById(R.id.RegistroDni)
        etNumero = findViewById(R.id.RegistroNumero)
        btnGuardar = findViewById(R.id.RegistroInvitado)

        val db = AppDatabase.getDatabase(this)
        val repo = RegistroInvitadoRepository(db)
        viewModel = RegistroInvitadoViewModel(repo)

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.mensaje?.let {
                    Toast.makeText(this@RegistroInvitadoActivity, it, Toast.LENGTH_SHORT).show()
                    if (it == "Invitado guardado") {
                        etNombre.text.clear()
                        etDni.text.clear()
                        etNumero.text.clear()
                    }
                }
            }
        }

        btnGuardar.setOnClickListener {
            viewModel.guardarInvitado(
                etNombre.text.toString(),
                etDni.text.toString(),
                etNumero.text.toString()
            )
        }
    }
}

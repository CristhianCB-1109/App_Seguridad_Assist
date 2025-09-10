package com.example.guardia

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.guardia.api.ApiClient
import com.example.guardia.api.Registro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistorialInvitado : AppCompatActivity() {

    private lateinit var tvNoHistorialInvitado: TextView
    private lateinit var lvHistorialInvitado: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_invitado)

        tvNoHistorialInvitado = findViewById(R.id.tvNoHistorialInvitado)
        lvHistorialInvitado = findViewById(R.id.lvHistorialInvitado)

        cargarHistorialInvitados()
    }

    private fun cargarHistorialInvitados() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.apiService.getHistorialInvitados()
                }

                if (response.isSuccessful) {
                    val historial = response.body()?.historial ?: emptyList()
                    if (historial.isNotEmpty()) {
                        mostrarHistorial(historial)
                        tvNoHistorialInvitado.visibility = TextView.GONE
                        lvHistorialInvitado.visibility = ListView.VISIBLE
                    } else {
                        tvNoHistorialInvitado.visibility = TextView.VISIBLE
                        lvHistorialInvitado.visibility = ListView.GONE
                    }
                } else {
                    Toast.makeText(this@HistorialInvitado, "Error al cargar historial: ${response.code()}", Toast.LENGTH_SHORT).show()
                    tvNoHistorialInvitado.visibility = TextView.VISIBLE
                    lvHistorialInvitado.visibility = ListView.GONE
                }
            } catch (e: Exception) {
                Log.e("HistorialInvitado", "Error de red o desconocido: ${e.message}", e)
                Toast.makeText(this@HistorialInvitado, "Error de red. Intenta de nuevo.", Toast.LENGTH_LONG).show()
                tvNoHistorialInvitado.visibility = TextView.VISIBLE
                lvHistorialInvitado.visibility = ListView.GONE
            }
        }
    }

    private fun mostrarHistorial(historial: List<Registro>) {
        val listaItems = historial.map {
            "DNI: ${it.dni_o_codigo}\nNombre: ${it.nombre}\nEntrada: ${it.hora_entrada}\nSalida: ${it.hora_salida ?: "N/A"}"
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listaItems
        )
        lvHistorialInvitado.adapter = adapter
    }
}
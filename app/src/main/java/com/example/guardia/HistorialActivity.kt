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

class HistorialActivity : AppCompatActivity() {

    private lateinit var tvNoHistorial: TextView
    private lateinit var lvHistorial: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        tvNoHistorial = findViewById(R.id.tvNoHistorial)
        lvHistorial = findViewById(R.id.lvHistorial)

        cargarHistorialAlumnos()
    }

    private fun cargarHistorialAlumnos() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.apiService.getHistorialAlumnos()
                }

                if (response.isSuccessful) {
                    val historial = response.body()?.historial ?: emptyList()
                    if (historial.isNotEmpty()) {
                        mostrarHistorial(historial)
                        tvNoHistorial.visibility = TextView.GONE
                        lvHistorial.visibility = ListView.VISIBLE
                    } else {
                        tvNoHistorial.visibility = TextView.VISIBLE
                        lvHistorial.visibility = ListView.GONE
                    }
                } else {
                    Toast.makeText(this@HistorialActivity, "Error al cargar historial: ${response.code()}", Toast.LENGTH_SHORT).show()
                    tvNoHistorial.visibility = TextView.VISIBLE
                    lvHistorial.visibility = ListView.GONE
                }
            } catch (e: Exception) {
                Log.e("HistorialActivity", "Error de red o desconocido: ${e.message}", e)
                Toast.makeText(this@HistorialActivity, "Error de red. Intenta de nuevo.", Toast.LENGTH_LONG).show()
                tvNoHistorial.visibility = TextView.VISIBLE
                lvHistorial.visibility = ListView.GONE
            }
        }
    }

    private fun mostrarHistorial(historial: List<Registro>) {
        val listaItems = historial.map {
            "Código: ${it.dni_o_codigo}\nNombre: ${it.nombre}\nEntrada: ${it.hora_entrada}\nSalida: ${it.hora_salida ?: "N/A"}"
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listaItems
        )
        lvHistorial.adapter = adapter
    }
}
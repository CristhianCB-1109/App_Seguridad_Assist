package com.example.guardia

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistorialActivity : AppCompatActivity() {

    private lateinit var rvHistorial: RecyclerView
    private lateinit var adapter: HistorialAdapter
    private lateinit var tvVacio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        rvHistorial = findViewById(R.id.rvHistorial)
        rvHistorial.layoutManager = LinearLayoutManager(this)

        tvVacio = findViewById(R.id.tvVacio)

        cargarHistorial()
    }

    override fun onResume() {
        super.onResume()
        cargarHistorial()
    }

    private fun cargarHistorial() {
        val dao = AppDatabase.getDatabase(this).registroAlumnoDao()

        CoroutineScope(Dispatchers.IO).launch {
            val lista = dao.obtenerHistorial()  // trae todos los registros
            withContext(Dispatchers.Main) {
                if (lista.isEmpty()) {
                    tvVacio.visibility = View.VISIBLE
                    rvHistorial.visibility = View.GONE
                } else {
                    tvVacio.visibility = View.GONE
                    rvHistorial.visibility = View.VISIBLE
                    adapter = HistorialAdapter(lista)
                    rvHistorial.adapter = adapter
                }
            }
        }
    }
}
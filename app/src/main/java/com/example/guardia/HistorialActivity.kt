package com.example.guardia

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        rvHistorial = findViewById(R.id.rvHistorial)
        rvHistorial.layoutManager = LinearLayoutManager(this)

        cargarHistorial()
    }

    private fun cargarHistorial() {
        val dao = AppDatabase.getDatabase(this).registroAlumnoDao()

        CoroutineScope(Dispatchers.IO).launch {
            val lista = dao.obtenerHistorial()  // trae todos los registros
            withContext(Dispatchers.Main) {
                adapter = HistorialAdapter(lista)
                rvHistorial.adapter = adapter
            }
        }
    }
}

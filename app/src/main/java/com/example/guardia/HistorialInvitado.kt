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

class HistorialInvitado : AppCompatActivity() {

    private lateinit var rvHistorialInvitado: RecyclerView
    private lateinit var adapter: HistorialAdapterInvitado
    private lateinit var tvVacioInvitado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.historial_invitado)

        rvHistorialInvitado = findViewById(R.id.rvHistorialInvitado)
        rvHistorialInvitado.layoutManager = LinearLayoutManager(this)

        tvVacioInvitado = findViewById(R.id.tvVacioInvitado)

        cargarHistorial()
    }

    override fun onResume() {
        super.onResume()
        cargarHistorial()
    }

    private fun cargarHistorial() {
        val dao = AppDatabase.getDatabase(this).registroInvitadoDao()

        CoroutineScope(Dispatchers.IO).launch {
            val lista = dao.obtenerTodos()  // trae todos los registros
            withContext(Dispatchers.Main) {
                if (lista.isEmpty()) {
                    tvVacioInvitado.visibility = View.VISIBLE
                    rvHistorialInvitado.visibility = View.GONE
                } else {
                    tvVacioInvitado.visibility = View.GONE
                    rvHistorialInvitado.visibility = View.VISIBLE
                    adapter = HistorialAdapterInvitado(lista)
                    rvHistorialInvitado.adapter = adapter
                }
            }
        }
    }
}


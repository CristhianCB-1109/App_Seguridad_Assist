package com.example.guardia.ui.Historial.Hist_invitado

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guardia.Data.Local.AppDatabase
import com.example.guardia.Data.Repository.HistorialInvitadoRepository
import com.example.guardia.R
import com.example.guardia.ui.Historial.HistorialInvitadoViewModel
import kotlinx.coroutines.launch

class HistorialInvitado : AppCompatActivity() {

    private lateinit var rvHistorialInvitado: RecyclerView
    private lateinit var adapter: HistorialAdapterInvitado
    private lateinit var tvVacioInvitado: TextView

    private lateinit var viewModel: HistorialInvitadoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.historial_invitado)

        rvHistorialInvitado = findViewById(R.id.rvHistorialInvitado)
        rvHistorialInvitado.layoutManager = LinearLayoutManager(this)

        tvVacioInvitado = findViewById(R.id.tvVacioInvitado)

        // Repositorio + ViewModel manual
        val db = AppDatabase.getDatabase(this)
        val repo = HistorialInvitadoRepository(db)
        viewModel = HistorialInvitadoViewModel(repo)

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state.lista.isEmpty()) {
                    tvVacioInvitado.visibility = View.VISIBLE
                    rvHistorialInvitado.visibility = View.GONE
                } else {
                    tvVacioInvitado.visibility = View.GONE
                    rvHistorialInvitado.visibility = View.VISIBLE
                    adapter = HistorialAdapterInvitado(state.lista)
                    rvHistorialInvitado.adapter = adapter
                }

                state.mensaje?.let {
                    tvVacioInvitado.text = it
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarHistorial()
    }
}



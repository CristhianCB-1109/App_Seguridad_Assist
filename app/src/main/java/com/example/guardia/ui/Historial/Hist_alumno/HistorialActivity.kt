package com.example.guardia.ui.Historial.Hist_alumno

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guardia.Data.Local.AppDatabase
import com.example.guardia.Data.Repository.HistorialRepository
import com.example.guardia.R
import kotlinx.coroutines.launch

class HistorialActivity : AppCompatActivity() {

    private lateinit var rvHistorial: RecyclerView
    private lateinit var adapter: HistorialAdapter
    private lateinit var tvVacio: TextView

    private lateinit var viewModel: HistorialViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        rvHistorial = findViewById(R.id.rvHistorial)
        rvHistorial.layoutManager = LinearLayoutManager(this)

        tvVacio = findViewById(R.id.tvVacio)

        val db = AppDatabase.getDatabase(this)
        val repo = HistorialRepository(db)
        viewModel = HistorialViewModel(repo)

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state.lista.isEmpty()) {
                    tvVacio.visibility = View.VISIBLE
                    rvHistorial.visibility = View.GONE
                } else {
                    tvVacio.visibility = View.GONE
                    rvHistorial.visibility = View.VISIBLE
                    adapter = HistorialAdapter(state.lista)
                    rvHistorial.adapter = adapter
                }

                state.mensaje?.let {
                    tvVacio.text = it
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarHistorial()
    }
}

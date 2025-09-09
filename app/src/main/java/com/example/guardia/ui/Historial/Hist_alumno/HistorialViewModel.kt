package com.example.guardia.ui.Historial.Hist_alumno

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardia.Data.Repository.HistorialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistorialViewModel(
    private val repository: HistorialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState: StateFlow<HistorialUiState> = _uiState

    fun cargarHistorial() {
        viewModelScope.launch {
            try {
                val registros = repository.obtenerHistorial()
                _uiState.value = _uiState.value.copy(
                    lista = registros,
                    mensaje = if (registros.isEmpty()) "No hay registros en el historial" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(mensaje = "Error al cargar historial")
            }
        }
    }
}

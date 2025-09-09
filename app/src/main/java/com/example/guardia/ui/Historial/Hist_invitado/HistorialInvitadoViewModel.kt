package com.example.guardia.ui.Historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardia.Data.Repository.HistorialInvitadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistorialInvitadoViewModel(
    private val repository: HistorialInvitadoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistorialInvitadoUiState())
    val uiState: StateFlow<HistorialInvitadoUiState> = _uiState

    fun cargarHistorial() {
        viewModelScope.launch {
            try {
                val registros = repository.obtenerHistorial()
                _uiState.value = _uiState.value.copy(
                    lista = registros,
                    mensaje = if (registros.isEmpty()) "No hay registros de invitados" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(mensaje = "Error al cargar historial de invitados")
            }
        }
    }
}

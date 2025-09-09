package com.example.guardia.ui.Seguridad.RegistroInvitado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardia.Data.Local.entities.RegistroInvitado
import com.example.guardia.Data.Repository.RegistroInvitadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistroInvitadoViewModel(
    private val repository: RegistroInvitadoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroInvitadoUiState())
    val uiState: StateFlow<RegistroInvitadoUiState> = _uiState

    fun guardarInvitado(nombre: String, dni: String, numero: String) {
        if (nombre.isBlank() || dni.isBlank() || numero.isBlank()) {
            _uiState.value = _uiState.value.copy(mensaje = "Completa todos los campos")
            return
        }

        viewModelScope.launch {
            val invitado = RegistroInvitado(nombre = nombre, dni = dni, numero = numero)
            repository.insertarInvitado(invitado)
            _uiState.value = RegistroInvitadoUiState(
                mensaje = "Invitado guardado"
            )
        }
    }
}

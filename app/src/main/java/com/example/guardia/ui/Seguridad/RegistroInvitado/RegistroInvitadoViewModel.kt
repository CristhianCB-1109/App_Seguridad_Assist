package com.example.guardia.ui.Seguridad.RegistroInvitado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardia.Data.Local.entities.RegistroInvitado
import com.example.guardia.Data.Repository.RegistroInvitadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        if (numero.length != 9) {
            _uiState.value = _uiState.value.copy(mensaje = "El número debe tener 9 dígitos")
            return
        }

        if (dni.length != 8) {
            _uiState.value = _uiState.value.copy(mensaje = "El DNI debe tener 8 dígitos")
            return
        }


        viewModelScope.launch {
            val fechaRegistro = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()) // Fecha de registro

            val invitado = RegistroInvitado(nombre = nombre, dni = dni, numero = numero, fechaRegistro = fechaRegistro)

            repository.insertarInvitado(invitado)
            _uiState.value = RegistroInvitadoUiState(
                mensaje = "Invitado guardado"
            )
        }

    }
}

package com.example.guardia.ui.Seguridad.RegistroAlumno

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardia.Data.Repository.AlumnoRepository
import com.example.guardia.Data.Repository.RegistroAlumnoRepository
import com.example.guardia.model.Alumno
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RegistrarAlumnoViewModel(
    private val alumnoRepository: AlumnoRepository,
    private val registroRepository: RegistroAlumnoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegistrarAlumnoUiState>(RegistrarAlumnoUiState())
    val uiState: StateFlow<RegistrarAlumnoUiState> = _uiState

    fun validarQR(qrData: String) {
        viewModelScope.launch {
            try {

                val alumno = alumnoRepository.getAlumno(qrData)

                _uiState.value = RegistrarAlumnoUiState(
                    alumno = Alumno(
                        id = alumno.id,
                        nombre = alumno.nombre,
                        carrera = alumno.carrera,
                        codigo = alumno.codigo_estudiante,
                        foto = alumno.foto
                    ),
                    qrValido = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    qrValido = false,
                    mensaje = "QR inválido"
                )
            }
        }
    }

    fun registrarEntradaSalida(alumno: Alumno) {
        viewModelScope.launch {
            val ultimoRegistro = registroRepository.obtenerUltimoRegistro(alumno.codigo)
            val horaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())

            if (ultimoRegistro != null && ultimoRegistro.fechasalida == null) {
                registroRepository.registrarSalida(ultimoRegistro.id, horaActual)
                _uiState.value = _uiState.value.copy(mensaje = "Salida registrada")
            } else {
                registroRepository.registrarEntrada(alumno)
                _uiState.value = _uiState.value.copy(mensaje = "Entrada registrada")
            }
        }
    }
}

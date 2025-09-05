package com.example.guardia.ui.Alumno

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardia.Data.Remote.AlumnoResponse
import com.example.guardia.Data.Repository.AlumnoRepository
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

class FaceAlumnoViewModel : ViewModel() {

    private val _uiState = MutableLiveData(FaceAlumnoUiState())
    val uiState: LiveData<FaceAlumnoUiState> = _uiState

    private val qrRefreshInterval = 40_000L // 40 segundos

    fun cargarAlumno(id: String) {
        _uiState.value = FaceAlumnoUiState(isLoading = true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Traer datos del repositorio (API o mock)
                val alumno = AlumnoRepository.getAlumno(id)

                // Generar QR inicial
                val qrBitmap = generarQR(alumno)

                _uiState.postValue(
                    FaceAlumnoUiState(
                        nombre = alumno.nombre,
                        codigo = alumno.codigo_estudiante,
                        carrera = alumno.carrera,
                        dni = alumno.dni,
                        telefono = alumno.telefono,
                        foto = alumno.foto,
                        qrBitmap = qrBitmap,
                        isLoading = false
                    )
                )

                // Mantener QR refrescándose
                startAutoRefreshQR(alumno)

            } catch (e: Exception) {
                _uiState.postValue(
                    FaceAlumnoUiState(
                        error = e.message,
                        isLoading = false
                    )
                )
            }
        }
    }

    private fun generarQR(alumno: AlumnoResponse): Bitmap? {
        return try {
            val timestamp = System.currentTimeMillis() / 1000
            val token = UUID.randomUUID().toString()

            val qrData = JSONObject().apply {
                put("id", alumno.id)
                put("nombre", alumno.nombre)
                put("carrera", alumno.carrera)
                put("codigo_estudiante", alumno.codigo_estudiante)
                put("foto", alumno.foto)
                put("timestamp", timestamp)
                put("token", token)
            }.toString()

            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.encodeBitmap(
                qrData,
                BarcodeFormat.QR_CODE,
                500,
                500
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun startAutoRefreshQR(alumno: AlumnoResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(qrRefreshInterval)

                val nuevoQR = generarQR(alumno)
                val estadoActual = _uiState.value

                if (estadoActual != null) {
                    _uiState.postValue(estadoActual.copy(qrBitmap = nuevoQR))
                }
            }
        }
    }
}

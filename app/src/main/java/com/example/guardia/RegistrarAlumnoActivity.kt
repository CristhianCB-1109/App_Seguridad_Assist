package com.example.guardia

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.guardia.api.ApiClient
import com.example.guardia.api.ApiService
import com.example.guardia.api.ApiResponse
import com.example.guardia.api.RegistroAlumnoRequest
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

class RegistrarAlumnoActivity : AppCompatActivity() {

    private lateinit var qrScannerView: DecoratedBarcodeView
    private val CAMERA_PERMISSION_CODE = 100
    private lateinit var apiService: ApiService

    // Evitar duplicados de QR consecutivos
    private val qrEscaneadosRecientemente = ConcurrentHashMap<String, Long>()
    private val TIEMPO_BLOQUEO_MS = 5000L // 5 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_alumno)

        qrScannerView = findViewById(R.id.qrScanner)
        apiService = ApiClient.apiService

        // Verificar permiso de cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            iniciarEscaner()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun iniciarEscaner() {
        qrScannerView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.text?.let { qrData ->
                    val ahora = System.currentTimeMillis()

                    // Evitar duplicados recientes
                    if (qrEscaneadosRecientemente[qrData]?.let { ahora - it < TIEMPO_BLOQUEO_MS } == true) {
                        return
                    }
                    qrEscaneadosRecientemente[qrData] = ahora

                    try {
                        val json = JSONObject(qrData)

                        val id = json.getString("id")
                        val nombre = json.getString("nombre")
                        val carrera = json.getString("carrera")
                        val codigo = json.getString("codigo_estudiante")
                        val foto = json.getString("foto")
                        val timestamp = json.getLong("timestamp")

                        if (isQRActivo(timestamp)) {
                            val alumno = Alumno(id, nombre, carrera, codigo, foto)
                            qrScannerView.pause()
                            mostrarDialogoConfirmacion(alumno)
                        } else {
                            Toast.makeText(this@RegistrarAlumnoActivity, "QR vencido", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@RegistrarAlumnoActivity, "QR inválido", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {}
        })
    }

    private fun isQRActivo(timestamp: Long): Boolean {
        val ahora = System.currentTimeMillis() / 1000
        return ahora - timestamp <= 40 // 40 segundos de validez
    }

    private fun mostrarDialogoConfirmacion(alumno: Alumno) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_alumno, null)

        val ivFoto = dialogView.findViewById<ImageView>(R.id.ivFoto)
        val tvNombre = dialogView.findViewById<TextView>(R.id.tvNombre)
        val tvCodigo = dialogView.findViewById<TextView>(R.id.tvCodigo)
        val tvCarrera = dialogView.findViewById<TextView>(R.id.tvCarrera)
        val btnRegistrar = dialogView.findViewById<Button>(R.id.btnRegistrar)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btnNoRegistrar)

        tvNombre.text = "Nombre: ${alumno.nombre}"
        tvCodigo.text = "Código: ${alumno.codigo}"
        tvCarrera.text = "Carrera: ${alumno.carrera}"

        if (alumno.foto.isNotEmpty()) {
            Glide.with(this).load(alumno.foto).into(ivFoto)
        } else {
            ivFoto.setImageResource(R.drawable.ic_person)
        }

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Llamamos a la API para verificar el estado actual del alumno (entrada/salida)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Endpoint para verificar si el alumno está dentro
                val response = apiService.obtenerUltimoRegistro(alumno.codigo)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        // El alumno ya tiene un registro de entrada sin salida
                        btnRegistrar.text = "Marcar salida"
                    } else {
                        // El alumno no está registrado o ya salió
                        btnRegistrar.text = "Marcar entrada"
                    }

                    btnRegistrar.setOnClickListener {
                        guardarRegistro(alumno)
                        dialog.dismiss()
                        qrScannerView.resume()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistrarAlumnoActivity, "Error al verificar estado: ${e.message}", Toast.LENGTH_LONG).show()
                    btnRegistrar.text = "Marcar entrada" // Por defecto, si hay un error
                    btnRegistrar.setOnClickListener {
                        guardarRegistro(alumno)
                        dialog.dismiss()
                        qrScannerView.resume()
                    }
                }
            }
        }

        btnCancelar.setOnClickListener {
            Toast.makeText(this@RegistrarAlumnoActivity, "No registrado", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            qrScannerView.resume()
        }
        dialog.show()
    }

    private fun guardarRegistro(alumno: Alumno) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val registroRequest = RegistroAlumnoRequest(
                    id_usuario = alumno.id,
                    nombre = alumno.nombre,
                    dni_o_codigo = alumno.codigo,
                    carrera = alumno.carrera
                )

                val response = apiService.registrarAlumno(registroRequest)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@RegistrarAlumnoActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                    } else {
                        val errorMessage = response.body()?.message ?: "Error desconocido"
                        Toast.makeText(this@RegistrarAlumnoActivity, "Error en el registro: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistrarAlumnoActivity, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistrarAlumnoActivity, "Error del servidor: ${e.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistrarAlumnoActivity, "Ocurrió un error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarEscaner()
            } else {
                Toast.makeText(this@RegistrarAlumnoActivity, "Se necesita el permiso de cámara", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        qrScannerView.resume()
    }

    override fun onPause() {
        super.onPause()
        qrScannerView.pause()
    }
}
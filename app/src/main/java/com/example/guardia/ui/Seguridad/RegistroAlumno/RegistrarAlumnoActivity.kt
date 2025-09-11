package com.example.guardia.ui.Seguridad.RegistroAlumno

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guardia.Data.Local.AppDatabase
import com.example.guardia.Data.Repository.AlumnoRepository
import com.example.guardia.Data.Repository.RegistroAlumnoRepository
import com.example.guardia.R
import com.example.guardia.model.Alumno
import com.google.zxing.ResultPoint
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap


class RegistrarAlumnoActivity : AppCompatActivity() {
    private lateinit var viewModel: RegistrarAlumnoViewModel
    private lateinit var qrScannerView: DecoratedBarcodeView
    private val CAMERA_PERMISSION_CODE = 100

    // Evitar duplicados de QR consecutivos
    private val qrEscaneadosRecientemente = ConcurrentHashMap<String, Long>()
    private val TIEMPO_BLOQUEO_MS = 5000L // 5 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_alumno)

        qrScannerView = findViewById(R.id.qrScanner)

        // Verificar permiso de cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
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

    override fun onResume(){
        super.onResume()
        qrScannerView.resume()
    }

    override fun onPause(){
        super.onPause()
        qrScannerView.pause()
    }
    private fun mostrarDialogoConfirmacion(alumno: Alumno) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_alumno, null)

        val tvNombre = dialogView.findViewById<TextView>(R.id.tvNombre)
        val tvCodigo = dialogView.findViewById<TextView>(R.id.tvCodigo)
        val tvCarrera = dialogView.findViewById<TextView>(R.id.tvCarrera)
        val btnRegistrar = dialogView.findViewById<Button>(R.id.btnRegistrar)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btnNoRegistrar)

        tvNombre.text = "Nombre: ${alumno.nombre}"
        tvCodigo.text = "Código: ${alumno.codigo}"
        tvCarrera.text = "Carrera: ${alumno.carrera}"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnRegistrar.setOnClickListener {
            viewModel.registrarEntradaSalida(alumno)
            dialog.dismiss()
            qrScannerView.resume()
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
            qrScannerView.resume()
        }

        dialog.show()
    }
}



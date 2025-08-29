package com.example.guardia

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class RegistrarAlumnoActivity : AppCompatActivity() {

    private lateinit var qrScannerView: DecoratedBarcodeView

    private val CAMERA_PERMISSION_CODE = 100

    //evitar duplicados de QR consecutivos
    private val qrEscaneadosRecientemente = ConcurrentHashMap<String, Long>()
    private val TIEMPO_BLOQUEO_MS = 5000L // 5 segundos para evitar duplicados

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_alumno)

        qrScannerView = findViewById(R.id.qrScanner)

        // verificar permiso de cámara
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

                    if (isQRActivo(qrData)) {
                        val alumno = obtenerDatosAlumno(qrData)
                        qrScannerView.pause() // Pausar escáner
                        mostrarDialogoConfirmacion(alumno)
                    } else {
                        Toast.makeText(
                            this@RegistrarAlumnoActivity,
                            "QR vencido",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {}
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarEscaner()
            } else {
                Toast.makeText(this, "Se necesita el permiso de cámara", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isQRActivo(qrData: String): Boolean {
        val partes = qrData.split("|")
        if (partes.size < 2) return false
        val timestamp = partes[1].toLongOrNull() ?: return false
        val ahora = System.currentTimeMillis() / 1000
        return ahora - timestamp <= 40
    }

    private fun obtenerDatosAlumno(qrData: String): Alumno {
        val alumnoId = qrData.split("|")[0]
        return Alumno(alumnoId, "Juan Pérez", "Ingeniería de Software")
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
        tvCodigo.text = "Código: ${alumno.id}"
        tvCarrera.text = "Carrera: ${alumno.carrera}"

        // Imagen por defecto (Despues API  )
        ivFoto.setImageResource(R.drawable.ic_person)

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnRegistrar.setOnClickListener {
            guardarRegistro(alumno.id, alumno.nombre, alumno.carrera)
            Toast.makeText(this, "Registro guardado", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            qrScannerView.resume()
        }

        btnCancelar.setOnClickListener {
            Toast.makeText(this, "No registrado", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            qrScannerView.resume()
        }

        dialog.show()
    }

    private fun guardarRegistro(codigo: String, nombre: String, carrera: String) {
        val dao = AppDatabase.getDatabase(this).registroAlumnoDao()

        val fechaHora = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            .format(java.util.Date())

        val registro = RegistroAlumno(
            codigo = codigo,
            nombre = nombre,
            carrera = carrera,
            fechaHora = fechaHora
        )

        CoroutineScope(Dispatchers.IO).launch {
            dao.insertarRegistro(registro)
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

data class Alumno(val id: String, val nombre: String, val carrera: String)





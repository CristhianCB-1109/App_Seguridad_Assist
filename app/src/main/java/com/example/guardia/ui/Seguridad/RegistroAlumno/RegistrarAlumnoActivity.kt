package com.example.guardia.ui.Seguridad.RegistroAlumno

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.guardia.Data.Local.AppDatabase
import com.example.guardia.Data.Repository.AlumnoRepository
import com.example.guardia.Data.Repository.RegistroAlumnoRepository
import com.example.guardia.R
import com.example.guardia.model.Alumno
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class RegistrarAlumnoActivity : AppCompatActivity() {

    private lateinit var qrScannerView: DecoratedBarcodeView
    private lateinit var viewModel: RegistrarAlumnoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_alumno)

        qrScannerView = findViewById(R.id.qrScanner)

        // Inyectar repositorios
        val db = AppDatabase.getDatabase(this)
        val registroRepo = RegistroAlumnoRepository(db)

        viewModel = RegistrarAlumnoViewModel(
            AlumnoRepository,
            registroRepo
        )

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                state.alumno?.let { alumno ->
                    if (state.qrValido) {
                        qrScannerView.pause()
                        mostrarDialogoConfirmacion(alumno)
                    }
                }

                state.mensaje?.let {
                    Toast.makeText(this@RegistrarAlumnoActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        iniciarEscaner()
    }

    private fun iniciarEscaner() {
        qrScannerView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.text?.let { qrData ->
                    viewModel.validarQR(qrData)
                }
            }
            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
        })
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


package com.example.guardia

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class FaceAlumnoActivity : ComponentActivity() {

    private lateinit var qrImage: ImageView
    private lateinit var tvNombre: TextView
    private lateinit var tvCodigo: TextView
    private lateinit var tvCarrera: TextView
    private lateinit var imgFoto: ImageView
    private lateinit var btnCerrarSesion: Button

    private val handler = Handler(Looper.getMainLooper())
    private val qrRefreshInterval = 40_000L // 40 segundos

    private lateinit var alumnoId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facealumno)

        qrImage = findViewById(R.id.qrImagen)
        tvNombre = findViewById(R.id.tvNombreAlumno)
        tvCodigo = findViewById(R.id.tvCodigoAlumno)
        tvCarrera = findViewById(R.id.tvCarreraAlumno)
        imgFoto = findViewById(R.id.imgFoto)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        alumnoId = intent.getStringExtra("id") ?: "A2025001"

        // Cargar datos del alumno
        CoroutineScope(Dispatchers.Main).launch {
            val alumno = AlumnoRepository.getAlumno(alumnoId)

            // Mostrar datos
            tvNombre.text = alumno.nombre
            tvCodigo.text = "Código: ${alumno.codigo_estudiante}"
            tvCarrera.text = "Carrera: ${alumno.carrera}"

            if (alumno.foto.isNotEmpty()) {
                Glide.with(this@FaceAlumnoActivity).load(alumno.foto).into(imgFoto)
            } else {
                imgFoto.setImageResource(R.drawable.ic_person)
            }

            // Generar QR con todos los datos
            generarQR(alumno)
            startAutoRefreshQR(alumno)
        }

        btnCerrarSesion.setOnClickListener {
            GestionActivity.cerrarSesion(this)
        }
    }

    // Funcion Generar QR
    private fun generarQR(alumno: AlumnoResponse) {
        try {
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
            val bitmap: Bitmap = barcodeEncoder.encodeBitmap(
                qrData,
                BarcodeFormat.QR_CODE,
                500,
                500
            )
            qrImage.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startAutoRefreshQR(alumno: AlumnoResponse) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                generarQR(alumno)
                handler.postDelayed(this, qrRefreshInterval)
            }
        }, qrRefreshInterval)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
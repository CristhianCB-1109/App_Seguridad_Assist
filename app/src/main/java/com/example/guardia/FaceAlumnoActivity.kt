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
import org.json.JSONObject
import java.util.*
import androidx.annotation.ColorInt

class FaceAlumnoActivity : ComponentActivity() {

    private lateinit var qrImage: ImageView
    private lateinit var tvNombre: TextView
    private lateinit var tvCodigo: TextView
    private lateinit var tvCarrera: TextView
    private lateinit var tvtelefono: TextView
    private lateinit var tvdni: TextView
    private lateinit var imgFoto: ImageView
    private lateinit var btnCerrarSesion: Button
    private lateinit var tvEstadoConexion: TextView

    private val handler = Handler(Looper.getMainLooper())
    private val qrRefreshInterval = 40_000L // 40 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facealumno)

        qrImage = findViewById(R.id.qrImagen)
        tvNombre = findViewById(R.id.tvNombreAlumno)
        tvCodigo = findViewById(R.id.tvCodigoAlumno)
        tvCarrera = findViewById(R.id.tvCarreraAlumno)
        tvdni = findViewById(R.id.tvdniAlumno)
        tvtelefono = findViewById(R.id.tvtelefonoAlumno)
        imgFoto = findViewById(R.id.imgFoto)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        tvEstadoConexion = findViewById(R.id.tvEstadoConexion)

        // Obtener los datos directamente del Intent
        val id = intent.getStringExtra("id") ?: ""
        val nombre = intent.getStringExtra("nombre") ?: ""
        val codigoEstudiante = intent.getStringExtra("codigo_estudiante") ?: ""
        val carrera = intent.getStringExtra("carrera") ?: ""
        val fotoUrl = intent.getStringExtra("fotoUrl") ?: ""
        val telefono = intent.getStringExtra("telefono") ?: ""
        val dni = intent.getStringExtra("dni") ?: ""
        val isMockData = intent.getBooleanExtra("isMockData", false)

        // Usar los datos del Intent para actualizar la UI
        tvNombre.text = nombre
        tvCodigo.text = "Código: $codigoEstudiante"
        tvCarrera.text = "Carrera: $carrera"
        tvdni.text = "DNI: $dni"
        tvtelefono.text = "Teléfono: $telefono"

        // Cargar la foto
        if (fotoUrl.isNotEmpty()) {
            Glide.with(this).load(fotoUrl).into(imgFoto)
        } else {
            imgFoto.setImageResource(R.drawable.ic_person)
        }

        // Mostrar el estado de la conexión
        if (isMockData) {
            tvEstadoConexion.text = "¡Sin conexión! Se muestran datos de prueba"
            tvEstadoConexion.setBackgroundColor(resources.getColor(R.color.red_500, null))
        } else {
            tvEstadoConexion.text = "Conexión exitosa. Datos del servidor"
            tvEstadoConexion.setBackgroundColor(resources.getColor(R.color.green_500, null))
        }

        // Generar QR y programar la actualización automática
        val alumnoData = AlumnoResponse(id, nombre, carrera, fotoUrl, codigoEstudiante, dni, telefono)
        generarQR(alumnoData)
        startAutoRefreshQR(alumnoData)

        btnCerrarSesion.setOnClickListener {
            GestionActivity.cerrarSesion(this)
        }
    }

    // El resto de las funciones (generarQR, startAutoRefreshQR, onDestroy) quedan iguales
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
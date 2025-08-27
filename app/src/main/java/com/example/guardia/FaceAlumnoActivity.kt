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
    private lateinit var alumnoNombre: String
    private lateinit var alumnoCarrera: String
    private lateinit var alumnoFotoUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facealumno)

        qrImage = findViewById(R.id.qrImagen)
        tvNombre = findViewById(R.id.tvNombreAlumno)
        tvCodigo = findViewById(R.id.tvCodigoAlumno)
        tvCarrera = findViewById(R.id.tvCarreraAlumno)
        imgFoto = findViewById(R.id.imgFoto)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        // Datos mock
        alumnoId = intent.getStringExtra("id") ?: "A2025001"
        alumnoNombre = intent.getStringExtra("nombre") ?: "Juan Pérez"
        alumnoCarrera = intent.getStringExtra("carrera") ?: "Ingeniería de Software"
        alumnoFotoUrl = intent.getStringExtra("fotoUrl") ?: "https://picsum.photos/200"

        tvNombre.text = alumnoNombre
        tvCodigo.text = "Código: $alumnoId"
        tvCarrera.text = "Carrera: $alumnoCarrera"

        // Cargar foto con Glide
        if (alumnoFotoUrl.isNotEmpty()) {
            Glide.with(this).load(alumnoFotoUrl).into(imgFoto)
        } else {
            imgFoto.setImageResource(R.drawable.ic_launcher_foreground)
        }
        //Generar qr
        generarQR()
        //Recargar qr cada 40 segundos
        startAutoRefreshQR()
        //boton cerrar sesion
        btnCerrarSesion.setOnClickListener {
            GestionActivity.cerrarSesion(this)
        }
    }

    private fun generarQR() {
        try {
            val timestamp = System.currentTimeMillis() / 1000
            val token = UUID.randomUUID().toString()
            val qrData = "$alumnoId|$timestamp|$token"

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

    private fun startAutoRefreshQR() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                generarQR()
                handler.postDelayed(this, qrRefreshInterval)
            }
        }, qrRefreshInterval)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}





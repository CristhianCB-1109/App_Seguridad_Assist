package com.example.guardia.ui.Alumno

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.guardia.R
import com.example.guardia.ui.Gestion.GestionActivity

class FaceAlumnoActivity : ComponentActivity() {

    private val viewModel: FaceAlumnoViewModel by viewModels()

    private lateinit var qrImage: ImageView
    private lateinit var tvNombre: TextView
    private lateinit var tvCodigo: TextView
    private lateinit var tvCarrera: TextView
    private lateinit var tvdni: TextView
    private lateinit var tvtelefono: TextView
    private lateinit var imgFoto: ImageView
    private lateinit var btnCerrarSesion: Button

    private lateinit var alumnoId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facealumno)

        // Referencias UI
        qrImage = findViewById(R.id.qrImagen)
        tvNombre = findViewById(R.id.tvNombreAlumno)
        tvCodigo = findViewById(R.id.tvCodigoAlumno)
        tvCarrera = findViewById(R.id.tvCarreraAlumno)
        tvdni = findViewById(R.id.tvdniAlumno)
        tvtelefono = findViewById(R.id.tvtelefonoAlumno)
        imgFoto = findViewById(R.id.imgFoto)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        alumnoId = intent.getStringExtra("id") ?: "A2025001"

        // Cargar alumno desde ViewModel
        viewModel.cargarAlumno(alumnoId)

        // Observar estado de la UI
        viewModel.uiState.observe(this, Observer { state ->
            if (state.isLoading) {
                // TODO: podrías mostrar un ProgressBar si quieres
            } else if (state.error != null) {
                tvNombre.text = "Error: ${state.error}"
            } else {
                tvNombre.text = state.nombre
                tvCodigo.text = "Código: ${state.codigo}"
                tvCarrera.text = "Carrera: ${state.carrera}"
                tvdni.text = "DNI: ${state.dni}"
                tvtelefono.text = "Telefono: ${state.telefono}"

                if (state.foto.isNotEmpty()) {
                    Glide.with(this).load(state.foto).into(imgFoto)
                } else {
                    imgFoto.setImageResource(R.drawable.ic_person)
                }

                qrImage.setImageBitmap(state.qrBitmap)
            }
        })

        // Botón cerrar sesión
        btnCerrarSesion.setOnClickListener {
            GestionActivity.cerrarSesion(this)
        }
    }
}

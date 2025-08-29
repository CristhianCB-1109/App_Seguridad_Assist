package com.example.guardia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistorialAdapter(private val lista: List<RegistroAlumno>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvCodigo: TextView = itemView.findViewById(R.id.tvCodigo)
        val tvCarrera: TextView = itemView.findViewById(R.id.tvCarrera)
        val tvFechaHora: TextView = itemView.findViewById(R.id.tvFechaHora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val registro = lista[position]
        holder.tvNombre.text = registro.nombre
        holder.tvCodigo.text = "Código: ${registro.codigo}"
        holder.tvCarrera.text = "Carrera: ${registro.carrera}"
        holder.tvFechaHora.text = registro.fechaHora
    }

    override fun getItemCount(): Int = lista.size
}

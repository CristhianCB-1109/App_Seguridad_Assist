package com.example.guardia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class HistorialAdapterInvitado(private val lista: List<RegistroInvitado>) :
    RecyclerView.Adapter<HistorialAdapterInvitado.HistorialViewHolder>() {

    class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val RegistroFullName: TextView = itemView.findViewById(R.id.RegistroFullName)
        val RegistroDni: TextView = itemView.findViewById(R.id.RegistroDni)
        val RegistroNumero: TextView = itemView.findViewById(R.id.RegistroNumero)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_invitado, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val registro = lista[position]
        holder.RegistroFullName.text = registro.nombre
        holder.RegistroDni.text = "Dni: ${registro.dni}"
        holder.RegistroNumero.text = "Numero: ${registro.numero}"

    }

    override fun getItemCount(): Int = lista.size
}



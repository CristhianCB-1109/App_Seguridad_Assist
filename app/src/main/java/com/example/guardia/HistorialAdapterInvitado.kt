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
        val tvnombre: TextView = itemView.findViewById(R.id.tvnombre)
        val tvdni: TextView = itemView.findViewById(R.id.tvdni)
        val tvnumero: TextView = itemView.findViewById(R.id.tvnumero)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_invitado, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val registro = lista[position]
        holder.tvnombre.text = registro.nombre
        holder.tvdni.text = "Dni: ${registro.dni}"
        holder.tvnumero.text = "Numero: ${registro.numero}"

    }

    override fun getItemCount(): Int = lista.size
}



package com.example.guardia.ui.Historial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guardia.Data.Local.entities.RegistroAlumno
import com.example.guardia.R
import java.text.SimpleDateFormat
import java.util.*

class HistorialAdapter(private val lista: List<RegistroAlumno>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvCodigo: TextView = itemView.findViewById(R.id.tvCodigo)
        val tvCarrera: TextView = itemView.findViewById(R.id.tvCarrera)
        val tvEntrada: TextView = itemView.findViewById(R.id.tvEntrada)
        val tvSalida: TextView = itemView.findViewById(R.id.tvSalida)
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

        val formatoOriginal = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formatoDeseado = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())

        // Entrada
        val entrada = try {
            val fecha = formatoOriginal.parse(registro.fechaentrada)
            formatoDeseado.format(fecha!!)
        } catch (e: Exception) {
            registro.fechaentrada
        }
        holder.tvEntrada.text = "Entrada: $entrada"

        // Salida
        if (registro.fechasalida != null) {
            val salida = try {
                val fecha = formatoOriginal.parse(registro.fechasalida)
                formatoDeseado.format(fecha!!)
            } catch (e: Exception) {
                registro.fechasalida
            }
            holder.tvSalida.text = "Salida: $salida"
        } else {
            holder.tvSalida.text = "Salida: ---"
        }
    }
    override fun getItemCount(): Int = lista.size
}





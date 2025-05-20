package com.utn.plannify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.utn.plannify.R
import com.utn.plannify.model.Actividad

class ActividadAdapter(private val listaActividades: List<Actividad>, private val onEditarClick: (Actividad) -> Unit,
                       private val onEliminarClick: (Actividad) -> Unit) :
    RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder>() {

    class ActividadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreActividad: TextView = itemView.findViewById(R.id.tvNombreActividad)
        val tvDescripcionActividad: TextView = itemView.findViewById(R.id.tvDescripcionActividad)
        val tvFechasActividad: TextView = itemView.findViewById(R.id.tvFechasActividad)
        val tvEstadoActividad: TextView = itemView.findViewById(R.id.tvEstadoActividad)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarActividad)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminarActividad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_actividad, parent, false)
        return ActividadViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        val actividad = listaActividades[position]
        holder.tvNombreActividad.text = actividad.nombre
        holder.tvDescripcionActividad.text = actividad.descripcion
        holder.tvFechasActividad.text = "Del ${actividad.fechaInicio} al ${actividad.fechaFin}"
        holder.tvEstadoActividad.text = "Estado: ${actividad.estado}"

        val context = holder.itemView.context
        val color = when (actividad.estado) {
            "Planificado" -> ContextCompat.getColor(context, R.color.estado_planificado)
            "En ejecución" -> ContextCompat.getColor(context, R.color.estado_en_ejecucion)
            "Realizado" -> ContextCompat.getColor(context, R.color.estado_realizado)
            else -> ContextCompat.getColor(context, android.R.color.darker_gray)
        }
        holder.tvEstadoActividad.setTextColor(color)
        holder.btnEditar.setOnClickListener { onEditarClick(actividad) }
        holder.btnEliminar.setOnClickListener { onEliminarClick(actividad) }
    }


    override fun getItemCount(): Int = listaActividades.size
}

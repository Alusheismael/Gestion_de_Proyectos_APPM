package com.utn.plannify.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.utn.plannify.R
import com.utn.plannify.model.Proyecto
import com.utn.plannify.ui.ActividadActivity

class ProyectoAdapter(
    private val listaProyectos: List<Proyecto>,
    private val launcher: ActivityResultLauncher<Intent> // Recibimos el launcher aquí
) : RecyclerView.Adapter<ProyectoAdapter.ProyectoViewHolder>() {

    class ProyectoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreProyecto: TextView = itemView.findViewById(R.id.tvNombreProyecto)
        val tvDescripcionProyecto: TextView = itemView.findViewById(R.id.tvDescripcionProyecto)
        val tvFechasProyecto: TextView = itemView.findViewById(R.id.tvFechasProyecto)
        val tvFechaFinProyecto: TextView = itemView.findViewById(R.id.tvFechaFinProyecto)
        val progressAvance: ProgressBar = itemView.findViewById(R.id.progressAvance)
        val tvPorcentaje: TextView = itemView.findViewById(R.id.tvPorcentaje)
        val tvProgresoLabel: TextView = itemView.findViewById(R.id.tvProgresoLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProyectoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_proyecto, parent, false)
        return ProyectoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProyectoViewHolder, position: Int) {
        val proyecto = listaProyectos[position]
        holder.tvNombreProyecto.text = proyecto.nombre
        holder.tvDescripcionProyecto.text = proyecto.descripcion
        holder.tvFechasProyecto.text = "Fecha Inicio: ${proyecto.fechaInicio}"
        holder.tvFechaFinProyecto.text = "Fecha Límite: ${proyecto.fechaFin}"
        holder.progressAvance.progress = proyecto.avance
        holder.tvPorcentaje.text = "${proyecto.avance}%"
        holder.tvProgresoLabel.text = "Progreso"

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ActividadActivity::class.java)
            intent.putExtra("proyectoId", proyecto.id)
            launcher.launch(intent) // Utilizamos el ActivityResultLauncher
        }
    }

    override fun getItemCount(): Int = listaProyectos.size
}

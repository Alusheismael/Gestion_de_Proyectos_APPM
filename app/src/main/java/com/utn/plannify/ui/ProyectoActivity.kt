package com.utn.plannify.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.utn.plannify.R
import com.utn.plannify.adapter.ProyectoAdapter
import com.utn.plannify.data.DBHelper
import com.utn.plannify.databinding.ActivityProyectoBinding
import com.utn.plannify.model.Proyecto
import java.util.Calendar

class ProyectoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProyectoBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var actividadLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProyectoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        // Registrar el ActivityResultLauncher para manejar el retorno de la ActividadActivity
        actividadLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            cargarProyectos() // Refresca la lista al volver
        }

        binding.rvProyectos.layoutManager = LinearLayoutManager(this)
        cargarProyectos()

        setupSpeedDial()
    }

    private fun setupSpeedDial() {
        val speedDial = binding.speedDial

        speedDial.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_nuevo, R.drawable.add2)
                .setLabel("Nuevo Proyecto")
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.white, null))
                .create()
        )
        speedDial.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_modificar, R.drawable.ic_edit)
                .setLabel("Modificar Proyecto")
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.white, null))
                .create()
        )
        speedDial.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_eliminar, R.drawable.ic_delete)
                .setLabel("Eliminar Proyecto")
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.white, null))
                .create()
        )

        speedDial.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_nuevo -> {
                    mostrarDialogoNuevoProyecto()
                    speedDial.close()
                    true
                }
                R.id.fab_modificar -> {
                    mostrarDialogoModificarProyecto()
                    speedDial.close()
                    true
                }
                R.id.fab_eliminar -> {
                    mostrarDialogoEliminarProyecto()
                    speedDial.close()
                    true
                }
                else -> false
            }
        }
    }

    private fun cargarProyectos() {
        val usuarioId = intent.getIntExtra("usuarioId", -1)
        val proyectos = dbHelper.obtenerProyectos(usuarioId).map { proyecto ->
            val nuevoAvance = dbHelper.calcularAvanceProyecto(proyecto.id)
            proyecto.copy(avance = nuevoAvance)
        }
        val adapter = ProyectoAdapter(proyectos, actividadLauncher)
        binding.rvProyectos.adapter = adapter
    }

    private fun mostrarDialogoNuevoProyecto() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_nuevo_proyecto, null)
        val nombreInput = view.findViewById<EditText>(R.id.etNombreProyecto)
        val descripcionInput = view.findViewById<EditText>(R.id.etDescripcion)
        val fechaInput = view.findViewById<EditText>(R.id.etFecha)
        val fechaFinInput = view.findViewById<EditText>(R.id.etFechaFin)
        val avanceSeekBar = view.findViewById<SeekBar>(R.id.seekAvance)

        fechaInput.setOnClickListener { showDatePicker(fechaInput) }
        fechaFinInput.setOnClickListener { showDatePicker(fechaFinInput) }

        AlertDialog.Builder(this)
            .setTitle("Nuevo Proyecto")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = nombreInput.text.toString()
                val descripcion = descripcionInput.text.toString()
                val fecha = fechaInput.text.toString()
                val fechaFin = fechaFinInput.text.toString()
                val avance = avanceSeekBar.progress
                val usuarioId = intent.getIntExtra("usuarioId", -1)

                if (nombre.isBlank() || fecha.isBlank() || usuarioId == -1) {
                    Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                } else {
                    dbHelper.insertarProyecto(nombre, descripcion, fecha, fechaFin, avance, usuarioId)
                    cargarProyectos()
                    Toast.makeText(this, "Proyecto guardado", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoModificarProyecto() {
        val usuarioId = intent.getIntExtra("usuarioId", -1)
        val proyectos = dbHelper.obtenerProyectos(usuarioId)

        if (proyectos.isEmpty()) {
            Toast.makeText(this, "No hay proyectos para modificar.", Toast.LENGTH_SHORT).show()
            return
        }

        val nombresProyectos = proyectos.map { "📁 ${it.nombre}" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Selecciona un Proyecto")
            .setItems(nombresProyectos) { _, index ->
                mostrarDialogoEditarProyecto(proyectos[index])
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEditarProyecto(proyecto: Proyecto) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_nuevo_proyecto, null)
        val nombreInput = view.findViewById<EditText>(R.id.etNombreProyecto)
        val descripcionInput = view.findViewById<EditText>(R.id.etDescripcion)
        val fechaInput = view.findViewById<EditText>(R.id.etFecha)
        val fechaFinInput = view.findViewById<EditText>(R.id.etFechaFin)
        val avanceSeekBar = view.findViewById<SeekBar>(R.id.seekAvance)

        nombreInput.setText(proyecto.nombre)
        descripcionInput.setText(proyecto.descripcion)
        fechaInput.setText(proyecto.fechaInicio)
        fechaFinInput.setText(proyecto.fechaFin)
        avanceSeekBar.progress = proyecto.avance

        fechaInput.setOnClickListener { showDatePicker(fechaInput) }
        fechaFinInput.setOnClickListener { showDatePicker(fechaFinInput) }

        AlertDialog.Builder(this)
            .setTitle("Modificar Proyecto")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = nombreInput.text.toString()
                val descripcion = descripcionInput.text.toString()
                val fecha = fechaInput.text.toString()
                val fechaFin = fechaFinInput.text.toString()
                val avance = avanceSeekBar.progress

                if (dbHelper.actualizarProyecto(proyecto.id, nombre, descripcion, fecha, fechaFin, avance)) {
                    Toast.makeText(this, "Proyecto actualizado", Toast.LENGTH_SHORT).show()
                    cargarProyectos()
                } else {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEliminarProyecto() {
        val usuarioId = intent.getIntExtra("usuarioId", -1)
        val proyectos = dbHelper.obtenerProyectos(usuarioId)

        if (proyectos.isEmpty()) {
            Toast.makeText(this, "No hay proyectos para eliminar.", Toast.LENGTH_SHORT).show()
            return
        }

        val nombresProyectos = proyectos.map { it.nombre }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Selecciona un Proyecto a Eliminar")
            .setItems(nombresProyectos) { _, which ->
                val proyecto = proyectos[which]
                AlertDialog.Builder(this)
                    .setTitle("¿Estás seguro?")
                    .setMessage("¿Deseas eliminar el proyecto \"${proyecto.nombre}\"?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        dbHelper.eliminarProyecto(proyecto.id)
                        cargarProyectos()
                        Toast.makeText(this, "Proyecto eliminado", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            editText.setText("$day/${month + 1}/$year")
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
}

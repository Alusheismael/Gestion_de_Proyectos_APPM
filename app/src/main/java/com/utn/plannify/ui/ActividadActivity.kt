package com.utn.plannify.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.utn.plannify.R
import com.utn.plannify.adapter.ActividadAdapter
import com.utn.plannify.data.DBHelper
import com.utn.plannify.databinding.ActivityActividadBinding
import com.utn.plannify.model.Actividad
import java.util.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent


class ActividadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActividadBinding
    private lateinit var dbHelper: DBHelper
    private var proyectoId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActividadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)
        proyectoId = intent.getIntExtra("proyectoId", -1)

        binding.rvActividades.layoutManager = LinearLayoutManager(this)
        cargarActividades()

        binding.fabAgregarActividad.setOnClickListener {
            mostrarDialogoNuevaActividad()
        }
    }

    private fun cargarActividades() {
        val actividades = dbHelper.obtenerActividades(proyectoId)
        val adapter = ActividadAdapter(
            actividades,
            onEditarClick = { actividad -> mostrarDialogoEditarActividad(actividad) },
            onEliminarClick = { actividad -> mostrarDialogoEliminarActividad(actividad) }
        )
        binding.rvActividades.adapter = adapter
    }

    private fun mostrarDialogoNuevaActividad() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_nueva_actividad, null)
        val nombreInput = view.findViewById<EditText>(R.id.etNombreActividad)
        val descripcionInput = view.findViewById<EditText>(R.id.etDescripcionActividad)
        val fechaInicioInput = view.findViewById<EditText>(R.id.etFechaInicio)
        val fechaFinInput = view.findViewById<EditText>(R.id.etFechaFin)
        val estadoSpinner = view.findViewById<Spinner>(R.id.spinnerEstado)

        val estados = arrayOf("Planificado", "En ejecución", "Realizado")
        estadoSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estados)

        fechaInicioInput.setOnClickListener { showDatePicker(fechaInicioInput) }
        fechaFinInput.setOnClickListener { showDatePicker(fechaFinInput) }

        AlertDialog.Builder(this)
            .setTitle("Nueva Actividad")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = nombreInput.text.toString()
                val descripcion = descripcionInput.text.toString()
                val fechaInicio = fechaInicioInput.text.toString()
                val fechaFin = fechaFinInput.text.toString()
                val estado = estadoSpinner.selectedItem.toString()

                if (nombre.isBlank() || fechaInicio.isBlank()) {
                    Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                } else {
                    dbHelper.insertarActividad(nombre, descripcion, fechaInicio, fechaFin, estado, proyectoId)
                    cargarActividades()
                    Toast.makeText(this, "Actividad guardada", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEditarActividad(actividad: Actividad) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_nueva_actividad, null)
        val nombreInput = view.findViewById<EditText>(R.id.etNombreActividad)
        val descripcionInput = view.findViewById<EditText>(R.id.etDescripcionActividad)
        val fechaInicioInput = view.findViewById<EditText>(R.id.etFechaInicio)
        val fechaFinInput = view.findViewById<EditText>(R.id.etFechaFin)
        val estadoSpinner = view.findViewById<Spinner>(R.id.spinnerEstado)

        val estados = arrayOf("Planificado", "En ejecución", "Realizado")
        estadoSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estados)

        // Prellenar campos
        nombreInput.setText(actividad.nombre)
        descripcionInput.setText(actividad.descripcion)
        fechaInicioInput.setText(actividad.fechaInicio)
        fechaFinInput.setText(actividad.fechaFin)
        val estadoIndex = estados.indexOf(actividad.estado)
        if (estadoIndex >= 0) estadoSpinner.setSelection(estadoIndex)

        fechaInicioInput.setOnClickListener { showDatePicker(fechaInicioInput) }
        fechaFinInput.setOnClickListener { showDatePicker(fechaFinInput) }

        AlertDialog.Builder(this)
            .setTitle("Modificar Actividad")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = nombreInput.text.toString()
                val descripcion = descripcionInput.text.toString()
                val fechaInicio = fechaInicioInput.text.toString()
                val fechaFin = fechaFinInput.text.toString()
                val estado = estadoSpinner.selectedItem.toString()

                if (nombre.isBlank() || estado.isBlank()) {
                    Toast.makeText(this, "Nombre y Estado son obligatorios", Toast.LENGTH_SHORT).show()
                } else {
                    val actualizado = dbHelper.actualizarActividad(
                        actividad.id, nombre, descripcion, fechaInicio, fechaFin, estado
                    )
                    if (actualizado) {
                        Toast.makeText(this, "Actividad actualizada", Toast.LENGTH_SHORT).show()
                        cargarActividades()
                    } else {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEliminarActividad(actividad: Actividad) {
        AlertDialog.Builder(this)
            .setTitle("¿Estás seguro?")
            .setMessage("¿Deseas eliminar la actividad \"${actividad.nombre}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                dbHelper.eliminarActividad(actividad.id)
                cargarActividades()
                Toast.makeText(this, "Actividad eliminada", Toast.LENGTH_SHORT).show()
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

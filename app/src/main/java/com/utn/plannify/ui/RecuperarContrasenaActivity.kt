package com.utn.plannify.ui

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.utn.plannify.R
import com.utn.plannify.data.DBHelper

class RecuperarContrasenaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_recuperar_contrasena)

        dbHelper = DBHelper(this)

        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val tvPregunta = findViewById<TextView>(R.id.tvPreguntaRecuperacion)
        val etRespuesta = findViewById<EditText>(R.id.etRespuesta)
        val btnBuscar = findViewById<Button>(R.id.btnBuscarPregunta)
        val btnValidar = findViewById<Button>(R.id.btnValidarRespuesta)

        btnBuscar.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            if (usuario.isNotBlank()) {
                val pregunta = dbHelper.obtenerPreguntaRecuperacion(usuario)
                if (pregunta != null) {
                    tvPregunta.text = pregunta
                    tvPregunta.visibility = TextView.VISIBLE
                    etRespuesta.visibility = EditText.VISIBLE
                    btnValidar.visibility = Button.VISIBLE
                } else {
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Ingrese el nombre de usuario", Toast.LENGTH_SHORT).show()
            }
        }

        btnValidar.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val respuesta = etRespuesta.text.toString().trim()
            if (respuesta.isNotBlank()) {
                if (dbHelper.verificarRespuesta(usuario, respuesta)) {
                    mostrarDialogoCambioContrasena(usuario)
                } else {
                    Toast.makeText(this, "Respuesta incorrecta", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Ingrese la respuesta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoCambioContrasena(usuario: String) {
        val view = layoutInflater.inflate(R.layout.dialog_cambiar_contrasena, null)
        val etNuevaContrasena = view.findViewById<EditText>(R.id.etNuevaContrasena)

        AlertDialog.Builder(this)
            .setTitle("Cambiar Contraseña")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevaContrasena = etNuevaContrasena.text.toString().trim()
                if (nuevaContrasena.isNotBlank()) {
                    val actualizado = dbHelper.actualizarContrasenaPorUsuario(usuario, nuevaContrasena)
                    if (actualizado) {
                        Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Ingrese una nueva contraseña", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

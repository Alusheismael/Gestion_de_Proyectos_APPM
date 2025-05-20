package com.utn.plannify.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.utn.plannify.data.DBHelper
import com.utn.plannify.databinding.ActivityRegistroBinding

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        binding.btnRegistrar.setOnClickListener {
            val nombres = binding.etNombres.text.toString().trim()
            val apellidos = binding.etApellidos.text.toString().trim()
            val nombreUsuario = binding.etNombreUsuario.text.toString().trim()
            val contrasena = binding.etPassword.text.toString().trim()
            val pregunta = binding.etPreguntaRecuperacion.text.toString().trim()
            val respuesta = binding.etRespuestaRecuperacion.text.toString().trim()

            if (nombres.isEmpty() || apellidos.isEmpty() || nombreUsuario.isEmpty() ||
                contrasena.isEmpty() || pregunta.isEmpty() || respuesta.isEmpty()
            ) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registrado = dbHelper.registrarUsuario(
                nombres, apellidos, nombreUsuario, contrasena, pregunta, respuesta
            )

            if (registrado) {
                Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "El nombre de usuario ya está registrado", Toast.LENGTH_LONG).show()
            }
        }
    }
}

package com.utn.plannify.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.utn.plannify.R
import com.utn.plannify.data.DBHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DBHelper(this)

        val usuarioInput = findViewById<EditText>(R.id.etCorreo) // es el campo de nombre de usuario
        val passInput = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegistro = findViewById<TextView>(R.id.btnRegistro)

        btnLogin.setOnClickListener {
            val nombreUsuario = usuarioInput.text.toString().trim()
            val contrasena = passInput.text.toString().trim()

            if (nombreUsuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = dbHelper.verificarUsuario(nombreUsuario, contrasena)

            if (usuario != null) {
                Toast.makeText(this, "Bienvenido, ${usuario.nombres}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("usuarioId", usuario.id)
                intent.putExtra("nombres", usuario.nombres)
                intent.putExtra("apellidos", usuario.apellidos)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
        val tvRecuperar = findViewById<TextView>(R.id.tvRecuperarContrasena)
        tvRecuperar.setOnClickListener {
            val intent = Intent(this, RecuperarContrasenaActivity::class.java)
            startActivity(intent)
        }

    }
}

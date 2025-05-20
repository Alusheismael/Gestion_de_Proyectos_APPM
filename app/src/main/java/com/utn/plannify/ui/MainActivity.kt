package com.utn.plannify.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.utn.plannify.R
import com.utn.plannify.data.DBHelper
import com.utn.plannify.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var usuarioId: Int = -1
    private var nombres: String = ""
    private var apellidos: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos del intent
        usuarioId = intent.getIntExtra("usuarioId", -1)
        nombres = intent.getStringExtra("nombres") ?: ""
        apellidos = intent.getStringExtra("apellidos") ?: ""

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Bienvenido, $nombres $apellidos", Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .setAnchorView(R.id.fab)
                .show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Mostrar nombre completo en el Navigation Drawer
        try {
            val headerView = navView.getHeaderView(0)
            val tvNombreUsuario = headerView.findViewById<TextView>(R.id.tvNombreUsuario)
            tvNombreUsuario.text = getString(R.string.bienvenida_usuario, nombres, apellidos)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Manejo de opciones de menú personalizadas
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_cambiar_nombre -> {
                    mostrarDialogoCambiarNombre()
                    true
                }
                R.id.nav_cambiar_contrasena -> {
                    mostrarDialogoCambiarContrasena()
                    true
                }
                R.id.nav_logout -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun mostrarDialogoCambiarNombre() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_cambiar_nombre, null)
        val input = dialogView.findViewById<EditText>(R.id.etNuevoNombre)

        AlertDialog.Builder(this)
            .setTitle("Cambiar nombre")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoNombre = input.text.toString().trim()

                if (nuevoNombre.isEmpty()) {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val dbHelper = DBHelper(this)
                val actualizado = dbHelper.actualizarNombre(usuarioId, nuevoNombre)

                if (actualizado) {
                    nombres = nuevoNombre
                    actualizarNombreEnHeader()
                    Toast.makeText(this, "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar el nombre", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoCambiarContrasena() {
        val input = EditText(this)
        input.hint = "Nueva contraseña"
        input.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        AlertDialog.Builder(this)
            .setTitle("Cambiar contraseña")
            .setView(input)
            .setPositiveButton("Cambiar") { _, _ ->
                val nuevaClave = input.text.toString().trim()

                if (nuevaClave.length < 4) {
                    Toast.makeText(this, "Debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val dbHelper = DBHelper(this)
                val actualizado = dbHelper.actualizarContrasena(usuarioId, nuevaClave)

                if (actualizado) {
                    Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarNombreEnHeader() {
        try {
            val headerView = binding.navView.getHeaderView(0)
            val tvNombreUsuario = headerView.findViewById<TextView>(R.id.tvNombreUsuario)
            tvNombreUsuario.text = getString(R.string.bienvenida_usuario, nombres, apellidos)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

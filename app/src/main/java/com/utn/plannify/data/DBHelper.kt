package com.utn.plannify.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.utn.plannify.model.Actividad
import com.utn.plannify.model.Usuario
import com.utn.plannify.model.Proyecto



class DBHelper(context: Context) : SQLiteOpenHelper(context, "PlannifyDB", null, 4) {

    override fun onCreate(db: SQLiteDatabase) {
        // Tabla de usuarios
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombres TEXT NOT NULL,
                apellidos TEXT NOT NULL,
                nombre_usuario TEXT UNIQUE NOT NULL,
                contrasena TEXT NOT NULL,
                pregunta_recuperacion TEXT NOT NULL,
                respuesta_recuperacion TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Tabla de proyectos
        db.execSQL(
            """
        CREATE TABLE IF NOT EXISTS proyectos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            descripcion TEXT,
            fechaInicio TEXT,
            fechaFin TEXT,
            avance INTEGER,
            usuario_id INTEGER NOT NULL,
            FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
        )
        """.trimIndent()
        )

        // Tabla de actividades
        db.execSQL(
            """
        CREATE TABLE IF NOT EXISTS actividades (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            descripcion TEXT,
            fecha_inicio TEXT,
            fecha_fin TEXT,
            estado TEXT CHECK(estado IN ('Planificado', 'En ejecución', 'Realizado')) DEFAULT 'Planificado',
            proyecto_id INTEGER NOT NULL,
            FOREIGN KEY (proyecto_id) REFERENCES proyectos(id)
        )
        """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS proyectos")
        onCreate(db)
    }

    // -------------------- USUARIOS ------------------------

    fun registrarUsuario(
        nombres: String,
        apellidos: String,
        nombreUsuario: String,
        contrasena: String,
        preguntaRecuperacion: String,
        respuestaRecuperacion: String
    ): Boolean {
        val db = writableDatabase
        return try {
            val stmt = db.compileStatement(
                """
            INSERT INTO usuarios 
            (nombres, apellidos, nombre_usuario, contrasena, pregunta_recuperacion, respuesta_recuperacion) 
            VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent()
            )
            stmt.bindString(1, nombres)
            stmt.bindString(2, apellidos)
            stmt.bindString(3, nombreUsuario)
            stmt.bindString(4, contrasena)
            stmt.bindString(5, preguntaRecuperacion)
            stmt.bindString(6, respuestaRecuperacion)
            stmt.executeInsert()
            true
        } catch (e: Exception) {
            false
        }
    }


    fun verificarUsuario(nombreUsuario: String, contrasena: String): Usuario? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE nombre_usuario = ? AND contrasena = ?",
            arrayOf(nombreUsuario, contrasena)
        )

        return if (cursor.moveToFirst()) {
            val usuario = Usuario(
                id = cursor.getInt(0),
                nombres = cursor.getString(1),
                apellidos = cursor.getString(2),
                nombreUsuario = cursor.getString(3),
                contrasena = cursor.getString(4)
            )
            cursor.close()
            usuario
        } else {
            cursor.close()
            null
        }
    }

    fun actualizarNombre(usuarioId: Int, nuevoNombre: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("nombres", nuevoNombre)
        val result = db.update("usuarios", values, "id = ?", arrayOf(usuarioId.toString()))
        return result > 0
    }

    fun actualizarContrasena(usuarioId: Int, nuevaClave: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("contrasena", nuevaClave)
        val result = db.update("usuarios", values, "id = ?", arrayOf(usuarioId.toString()))
        return result > 0
    }

    // -------------------- PROYECTOS ------------------------

    fun insertarProyecto(nombre: String, descripcion: String, fechaInicio: String, fechaFin: String, avance: Int, usuarioId: Int): Boolean {
        val db = writableDatabase
        return try {
            val stmt = db.compileStatement(
                "INSERT INTO proyectos (nombre, descripcion, fechaInicio, fechaFin, avance, usuario_id) VALUES (?, ?, ?, ?, ?, ?)"
            )
            stmt.bindString(1, nombre)
            stmt.bindString(2, descripcion)
            stmt.bindString(3, fechaInicio)
            stmt.bindString(4, fechaFin)
            stmt.bindLong(5, avance.toLong())
            stmt.bindLong(6, usuarioId.toLong())
            stmt.executeInsert()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    fun obtenerProyectos(usuarioId: Int): List<Proyecto> {
        val lista = mutableListOf<Proyecto>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM proyectos WHERE usuario_id = ?",
            arrayOf(usuarioId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val proyecto = Proyecto(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("fechaInicio")),
                    fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("fechaFin")),
                    avance = cursor.getInt(cursor.getColumnIndexOrThrow("avance"))
                )
                lista.add(proyecto)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
    fun actualizarProyecto(proyectoId: Int, nombre: String, descripcion: String, fecha: String, fechaFin: String, avance: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("fechaInicio", fecha)
            put("fechaFin", fechaFin)
            put("avance", avance)
        }
        val result = db.update("proyectos", values, "id = ?", arrayOf(proyectoId.toString()))
        return result > 0
    }
    fun eliminarProyecto(proyectoId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete("proyectos", "id = ?", arrayOf(proyectoId.toString()))
        return result > 0
    }
    fun insertarActividad(nombre: String, descripcion: String, fechaInicio: String, fechaFin: String, estado: String, proyectoId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("fecha_inicio", fechaInicio)
            put("fecha_fin", fechaFin)
            put("estado", estado)
            put("proyecto_id", proyectoId)
        }
        return db.insert("actividades", null, values) != -1L
    }

    fun obtenerActividades(proyectoId: Int): List<Actividad> {
        val lista = mutableListOf<Actividad>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM actividades WHERE proyecto_id = ?", arrayOf(proyectoId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val actividad = Actividad(
                    id = cursor.getInt(0),
                    nombre = cursor.getString(1),
                    descripcion = cursor.getString(2),
                    fechaInicio = cursor.getString(3),
                    fechaFin = cursor.getString(4),
                    estado = cursor.getString(5),
                    proyectoId = cursor.getInt(6)
                )
                lista.add(actividad)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun actualizarActividad(id: Int, nombre: String, descripcion: String, fechaInicio: String, fechaFin: String, estado: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("fecha_inicio", fechaInicio)
            put("fecha_fin", fechaFin)
            put("estado", estado)
        }
        val result = db.update("actividades", values, "id = ?", arrayOf(id.toString()))
        return result > 0
    }
    fun eliminarActividad(id: Int): Boolean {
        val db = writableDatabase
        val result = db.delete("actividades", "id = ?", arrayOf(id.toString()))
        return result > 0
    }
    fun calcularAvanceProyecto(proyectoId: Int): Int {
        val db = readableDatabase
        val totalQuery = "SELECT COUNT(*) FROM actividades WHERE proyecto_id = ?"
        val realizadasQuery = "SELECT COUNT(*) FROM actividades WHERE proyecto_id = ? AND estado = 'Realizado'"

        val totalCursor = db.rawQuery(totalQuery, arrayOf(proyectoId.toString()))
        val realizadasCursor = db.rawQuery(realizadasQuery, arrayOf(proyectoId.toString()))

        val total = if (totalCursor.moveToFirst()) totalCursor.getInt(0) else 0
        val realizadas = if (realizadasCursor.moveToFirst()) realizadasCursor.getInt(0) else 0

        totalCursor.close()
        realizadasCursor.close()

        if (total == 0) return 0
        return (realizadas * 100) / total
    }
    fun obtenerPreguntaRecuperacion(nombreUsuario: String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT pregunta_recuperacion FROM usuarios WHERE nombre_usuario = ?", arrayOf(nombreUsuario))
        return if (cursor.moveToFirst()) {
            val pregunta = cursor.getString(0)
            cursor.close()
            pregunta
        } else {
            cursor.close()
            null
        }
    }

    fun verificarRespuesta(nombreUsuario: String, respuesta: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuarios WHERE nombre_usuario = ? AND respuesta_recuperacion = ?", arrayOf(nombreUsuario, respuesta))
        val valido = cursor.moveToFirst()
        cursor.close()
        return valido
    }
    fun actualizarContrasenaPorUsuario(nombreUsuario: String, nuevaContrasena: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("contrasena", nuevaContrasena)
        }
        val result = db.update("usuarios", values, "nombre_usuario = ?", arrayOf(nombreUsuario))
        return result > 0
    }


}

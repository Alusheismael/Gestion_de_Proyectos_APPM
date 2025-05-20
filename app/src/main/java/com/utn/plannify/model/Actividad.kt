package com.utn.plannify.model

data class Actividad(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val fechaInicio: String,
    val fechaFin: String,
    val estado: String,
    val proyectoId: Int
)

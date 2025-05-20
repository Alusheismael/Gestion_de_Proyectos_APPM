package com.utn.plannify.model

data class Proyecto(
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val fechaInicio: String,
    val fechaFin: String,
    val avance: Int
)

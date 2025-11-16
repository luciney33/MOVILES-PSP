package com.example.navigation.data.local.entity

import androidx.room.ColumnInfo

// POJO que mapea el resultado del JOIN: no es una entidad de Room, solo un data class para consultas
data class SesionEjercicioConName(
    val id: Int,
    val sesionId: Int,
    val ejercicioId: Int,
    @ColumnInfo(name = "nombreEjercicio")
    val nombreEjercicio: String,
    val orden: Int,
    val volumenKg: Double,
    val notas: String,
    // campos de la relación entrenamiento_ejercicio
    val series: Int = 0,
    val repeticiones: Int = 0
)

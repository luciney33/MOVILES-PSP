package com.example.navigation.data.local.entity

import androidx.room.ColumnInfo

data class SesionEjercicioConName(
    val id: Int,
    val sesionId: Int,
    val ejercicioId: Int,
    @ColumnInfo(name = "nombreEjercicio")
    val nombreEjercicio: String,
    val orden: Int,
    val volumenKg: Double,
    val notas: String,
    val series: Int = 0,
    val repeticiones: Int = 0
)

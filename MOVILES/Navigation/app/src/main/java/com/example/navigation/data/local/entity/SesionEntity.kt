package com.example.navigation.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sesiones",
    foreignKeys = [
        ForeignKey(
            entity = EntrenamientoEntity::class,
            parentColumns = ["id"],
            childColumns = ["entrenamientoId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
)
data class SesionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int ,
    val entrenamientoId: Int,
    val fechaInicio: Long = System.currentTimeMillis(),
    val duracionMs: Long,
    val ejerciciosCompletados: Int ,
    val notas: String
)


package com.example.navigation.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
    val id: Int = 0,
    val entrenamientoId: Int? = null,
    val fechaInicio: Long = System.currentTimeMillis(),
    val duracionMs: Long? = null,
    val ejerciciosCompletados: Int = 0,
    val notas: String? = null
)


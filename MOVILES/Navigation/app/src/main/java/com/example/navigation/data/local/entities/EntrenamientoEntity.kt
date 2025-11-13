package com.example.navigation.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.navigation.domain.model.Entrenamiento

@Entity(
    tableName = "entrenamientos"
)
data class EntrenamientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val ultimaActualizacion: Long? = null
)

fun EntrenamientoEntity.toEntrenamiento() = Entrenamiento(
    id = this.id,
    nombre = this.nombre,
    descripcion = this.descripcion,
)

fun Entrenamiento.toEntrenamientoEntity() = EntrenamientoEntity(
    id = this.id,
    nombre = this.nombre,
    descripcion = this.descripcion,
)

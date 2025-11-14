package com.example.navigation.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.navigation.domain.model.Ejercicio

@Entity(
    tableName = "ejercicios"
)
data class EjercicioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val nombre: String,
    val grupoMuscular: String,
)

fun EjercicioEntity.toEjercicio(): Ejercicio {
    return Ejercicio(
        id = this.id,
        nombre = this.nombre,
        grupoMuscular = this.grupoMuscular
    )
}

fun Ejercicio.toEjercicioEntity(): EjercicioEntity {
    return EjercicioEntity(
        id = this.id,
        nombre = this.nombre,
        grupoMuscular = this.grupoMuscular
    )
}
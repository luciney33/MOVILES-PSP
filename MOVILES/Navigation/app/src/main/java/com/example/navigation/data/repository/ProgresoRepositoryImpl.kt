package com.example.navigation.data.repository

import com.example.navigation.data.constants.Constantes
import com.example.navigation.data.local.dao.EjercicioDao
import com.example.navigation.data.local.dao.SesionEjercicioDao
import com.example.navigation.domain.model.Ejercicio
import com.example.navigation.domain.model.Progreso
import javax.inject.Inject

class ProgresoRepositoryImpl @Inject constructor(
    private val ejercicioDao: EjercicioDao,
    private val sesionEjercicioDao: SesionEjercicioDao
) {

    suspend fun getAllEjercicios(): List<Ejercicio> {
        return ejercicioDao.getAllEjercicios().map { e ->
            Ejercicio(e.id, e.nombre, e.grupoMuscular, e.descripcion, e.iconoResName)
        }
    }

    suspend fun getProgresoSummaryForEjercicio(ejercicioId: Int): Progreso? {
        val ultimo = sesionEjercicioDao.getLastByEjercicioIdConName(ejercicioId) ?: return null
        val maxVolumen = sesionEjercicioDao.getMaxVolumenByEjercicioId(ejercicioId)
        val top2 = sesionEjercicioDao.getTop2ByEjercicioIdConName(ejercicioId)

        val tendenciaUp = top2.size >= 2 && top2[0].volumenKg > top2[1].volumenKg

        val ultimoPeso: String
        val ultimoDetalle: String

        if (ultimo.volumenKg > 0.0) {
            ultimoPeso = "${ultimo.volumenKg}${Constantes.KG_SUFFIX}"
            ultimoDetalle = if (ultimo.notas.isNotBlank()) ultimo.notas else ultimoPeso
        } else {
            val notasTrim = ultimo.notas.trim()
            if (notasTrim.isNotEmpty()) {
                if (notasTrim.matches(Constantes.NUMERIC_REGEX)) {
                    ultimoPeso = "$notasTrim${Constantes.REPS_SUFFIX}"
                    ultimoDetalle = notasTrim
                } else {
                    ultimoPeso = notasTrim
                    ultimoDetalle = notasTrim
                }
            } else if (ultimo.series > 0 && ultimo.repeticiones > 0) {
                ultimoPeso = "${ultimo.series} x ${ultimo.repeticiones}"
                ultimoDetalle = ultimoPeso
            } else {
                ultimoPeso = Constantes.NO_DATA
                ultimoDetalle = Constantes.NO_DATA
            }
        }

        val recordStr = if (maxVolumen == null || maxVolumen == 0.0) Constantes.NO_DATA else "$maxVolumen${Constantes.KG_SUFFIX}"

        return Progreso(
            ejercicioId = ultimo.ejercicioId,
            nombre = ultimo.nombreEjercicio,
            grupoMuscular = "",
            ultimoPeso = ultimoPeso,
            ultimoDetalle = ultimoDetalle,
            record = recordStr,
            tendenciaUp = tendenciaUp
        )
    }

    suspend fun getAllProgresoSummaries(): List<Progreso> {
        val ejercicios = getAllEjercicios()
        return ejercicios.map { ej ->
            val summary = getProgresoSummaryForEjercicio(ej.id)
            summary?.copy(grupoMuscular = ej.grupoMuscular) ?: emptyProgresoFor(ej)
        }
    }

    private fun emptyProgresoFor(ej: Ejercicio) = Progreso(
        ejercicioId = ej.id,
        nombre = ej.nombre,
        grupoMuscular = ej.grupoMuscular,
        ultimoPeso = Constantes.NO_DATA,
        ultimoDetalle = Constantes.NO_DATA,
        record = Constantes.NO_DATA,
        tendenciaUp = false
    )

}

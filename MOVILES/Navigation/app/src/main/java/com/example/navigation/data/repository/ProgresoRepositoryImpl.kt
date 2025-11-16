package com.example.navigation.data.repository

import android.util.Log
import com.example.navigation.data.local.dao.EjercicioDao
import com.example.navigation.data.local.dao.SesionEjercicioDao
import com.example.navigation.domain.model.Ejercicio
import com.example.navigation.domain.model.Progreso
import javax.inject.Inject

class ProgresoRepositoryImpl @Inject constructor(
    private val ejercicioDao: EjercicioDao,
    private val sesionEjercicioDao: SesionEjercicioDao
)  {

     suspend fun getAllEjercicios(): List<Ejercicio> {
        return try {
            val list = ejercicioDao.getAllEjercicios().map { e -> Ejercicio(e.id, e.nombre, e.grupoMuscular, e.descripcion, e.iconoResName) }
            list
        } catch (e: Exception) {
            Log.w("ProgresoRepo", "Error getAllEjercicios", e)
            emptyList()
        }
    }

     suspend fun getProgresoSummaryForEjercicio(ejercicioId: Int): Progreso? {
        return try {
            val last = sesionEjercicioDao.getLastByEjercicioIdConName(ejercicioId)
            if (last == null) {
                return null
            }

            val max = sesionEjercicioDao.getMaxVolumenByEjercicioId(ejercicioId)

            // obtener top2 para tendencia
            val top2 = sesionEjercicioDao.getTop2ByEjercicioIdConName(ejercicioId)
            val tendenciaUp = if (top2.size >= 2) {
                val a = top2[0].volumenKg
                val b = top2[1].volumenKg
                a > b
            } else {
                false
            }

            // Formateo de último registro: si volumenKg > 0 -> mostrar peso; si es 0 -> intentar usar notas o series/repeticiones
            val ultimoPesoStr: String
            val ultimoDetalle: String

            if (last.volumenKg > 0.0) {
                ultimoPesoStr = "${last.volumenKg} kg"
                ultimoDetalle = if (last.notas.isNotEmpty()) last.notas else ultimoPesoStr
            } else {
                // volumen 0 (bodyweight o solo reps) -> priorizar notas si contienen texto significativo
                val notas = last.notas.trim()
                if (notas.isNotEmpty()) {
                    ultimoDetalle = notas
                    ultimoPesoStr = if (notas.matches(Regex("^\\d+$"))) "${notas} reps" else notas
                } else if (last.series > 0 && last.repeticiones > 0) {
                    ultimoPesoStr = "${last.series} x ${last.repeticiones}"
                    ultimoDetalle = ultimoPesoStr
                } else {
                    ultimoPesoStr = "-"
                    ultimoDetalle = "-"
                }
            }

            // Formateo record: si max es null o 0.0 -> mostrar '-'
            val recordStr = if (max == null || max == 0.0) "-" else "${max} kg"

            return Progreso(
                ejercicioId = last.ejercicioId,
                nombre = last.nombreEjercicio,
                grupoMuscular = "", // se completará en getAllProgresoSummaries cuando se conozca
                ultimoPeso = ultimoPesoStr,
                ultimoDetalle = ultimoDetalle,
                record = recordStr,
                tendenciaUp = tendenciaUp
            )
        } catch (e: Exception) {
            Log.w("ProgresoRepo", "Error getProgresoSummaryForEjercicio", e)
            null
        }
    }

     suspend fun getAllProgresoSummaries(): List<Progreso> {
        return try {
            val ejercicios = getAllEjercicios()
            val result = ejercicios.map { ej ->
                val summary = getProgresoSummaryForEjercicio(ej.id)
                if (summary != null) {
                    summary.copy(grupoMuscular = ej.grupoMuscular)
                } else {
                    // crear placeholder si no hay historial
                    Progreso(
                        ejercicioId = ej.id,
                        nombre = ej.nombre,
                        grupoMuscular = ej.grupoMuscular,
                        ultimoPeso = "-",
                        ultimoDetalle = "-",
                        record = "-",
                        tendenciaUp = false
                    )
                }
            }
            result
        } catch (e: Exception) {
            Log.w("ProgresoRepo", "Error getAllProgresoSummaries", e)
            emptyList()
        }
    }

}

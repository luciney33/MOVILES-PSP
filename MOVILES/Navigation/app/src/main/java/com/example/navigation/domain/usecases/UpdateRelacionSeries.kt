package com.example.navigation.domain.usecases

import android.util.Log
import com.example.navigation.data.repository.SesionRepositoryImpl
import javax.inject.Inject

class UpdateRelacionSeries @Inject constructor(
    private val repo: SesionRepositoryImpl
) {
    suspend operator fun invoke(sesionId: Int, ejercicioId: Int, series: Int, repeticiones: Int): Int = try {
        // obtener entrenamientoId desde la sesion
        val sesion = repo.getSesionById(sesionId)
        val entrenamientoId = sesion?.entrenamientoId ?: return -1
        repo.updateRelationSeries(entrenamientoId, ejercicioId, series, repeticiones)
    } catch (e: Exception) {
        Log.w("UpdateRelationSeries","Error updating relation for sesionId=$sesionId ejer=$ejercicioId", e)
        -1
    }
}


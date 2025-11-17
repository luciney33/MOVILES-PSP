package com.example.navigation.domain.usecases

import com.example.navigation.data.repository.SesionRepositoryImpl
import javax.inject.Inject

class UpdateRelacionSeries @Inject constructor(
    private val repo: SesionRepositoryImpl
) {
    suspend operator fun invoke(sesionId: Int, ejercicioId: Int, series: Int, repeticiones: Int): Int {
        val sesion = repo.getSesionById(sesionId)
        val entrenamientoId = sesion?.entrenamientoId ?: return -1
     return repo.updateRelationSeries(entrenamientoId, ejercicioId, series, repeticiones)
    }

}


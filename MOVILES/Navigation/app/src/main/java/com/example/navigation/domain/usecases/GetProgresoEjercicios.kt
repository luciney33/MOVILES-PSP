package com.example.navigation.domain.usecases

import android.util.Log
import com.example.navigation.data.repository.ProgresoRepositoryImpl
import com.example.navigation.domain.model.Progreso
import javax.inject.Inject

class GetProgresoEjercicios @Inject constructor(
    private val repo: ProgresoRepositoryImpl
) {
    suspend operator fun invoke(): List<Progreso> = try {
        repo.getAllProgresoSummaries()
    } catch (e: Exception) {
        Log.w("GetProgresoEjercicios","Error obteniendo progreso", e)
        emptyList()
    }
}

package com.example.navigation.domain.usecases

import android.util.Log
import com.example.navigation.data.repository.SesionRepositoryImpl
import com.example.navigation.domain.model.SesionEjercicio
import javax.inject.Inject

class GetEjerciciosBySesion @Inject constructor(
    private val repo: SesionRepositoryImpl
) {
    suspend operator fun invoke(sesionId: Int): List<SesionEjercicio> = try {
        repo.getEjerciciosBySesionId(sesionId)
    } catch (e: Exception) {
        Log.w("GetEjerciciosBySesion", "Error fetching ejercicios for sesionId=$sesionId", e)
        emptyList()
    }
}
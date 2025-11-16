package com.example.navigation.domain.usecases

import android.util.Log
import com.example.navigation.data.repository.SesionRepositoryImpl
import javax.inject.Inject

class UpdateSesionDuracion @Inject constructor(
    private val repo: SesionRepositoryImpl
) {
    suspend operator fun invoke(sesionId: Int, duracionMs: Long): Int = try {
        repo.updateSesionDuracion(sesionId, duracionMs)
    } catch (e: Exception) {
        Log.w("UpdateSesionDuracion", "Error updating duracion for sesion=$sesionId", e)
        -1
    }
}


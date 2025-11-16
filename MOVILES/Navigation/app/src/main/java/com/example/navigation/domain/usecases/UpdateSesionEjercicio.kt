package com.example.navigation.domain.usecases

import android.util.Log
import com.example.navigation.data.repository.SesionRepositoryImpl
import com.example.navigation.domain.model.SesionEjercicio
import javax.inject.Inject

class UpdateSesionEjercicio @Inject constructor(
    private val repo: SesionRepositoryImpl
) {
    suspend operator fun invoke(domain: SesionEjercicio): Int = try {
        repo.updateSesionEjercicio(domain)
    } catch (e: Exception) {
        Log.w("UpdateSesionEjercicio", "Error updating sesionEjercicio id=${domain.id}", e)
        -1
    }
}

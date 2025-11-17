package com.example.navigation.domain.usecases

import com.example.navigation.data.repository.SesionRepositoryImpl
import javax.inject.Inject

class UpdateSesionDuracion @Inject constructor(
    private val repo: SesionRepositoryImpl
) {
    suspend operator fun invoke(sesionId: Int, duracionMs: Long): Int = repo.updateSesionDuracion(sesionId, duracionMs)

}


package com.example.navigation.domain.usecases

import com.example.navigation.data.repository.SesionRepositoryImpl
import com.example.navigation.domain.model.SesionEjercicio
import javax.inject.Inject

class UpdateSesionEjercicio @Inject constructor(
    private val repo: SesionRepositoryImpl
) {
    suspend operator fun invoke(domain: SesionEjercicio): Int = repo.updateSesionEjercicio(domain)

}

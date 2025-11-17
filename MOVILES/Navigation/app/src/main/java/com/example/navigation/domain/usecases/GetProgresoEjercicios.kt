package com.example.navigation.domain.usecases

import com.example.navigation.data.repository.ProgresoRepositoryImpl
import com.example.navigation.domain.model.Progreso
import javax.inject.Inject

class GetProgresoEjercicios @Inject constructor(
    private val repo: ProgresoRepositoryImpl
) {
    suspend operator fun invoke(): List<Progreso> = repo.getAllProgresoSummaries()

}

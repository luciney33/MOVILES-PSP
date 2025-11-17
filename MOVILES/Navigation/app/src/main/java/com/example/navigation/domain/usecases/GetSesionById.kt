package com.example.navigation.domain.usecases

import com.example.navigation.data.repository.SesionRepositoryImpl
import com.example.navigation.domain.model.Sesion
import javax.inject.Inject

class GetSesionById @Inject constructor(
    private val repo: SesionRepositoryImpl
) {
    suspend operator fun invoke(id: Int): Sesion? = repo.getSesionById(id)

}
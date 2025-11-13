package com.example.navigation.data.repositories

import com.example.navigation.data.local.dao.EjercicioDao
import jakarta.inject.Inject

class EjercicioRepository@Inject constructor(
    private val ejercicioDao : EjercicioDao
) {
    suspend fun getAllEjercicios() = ejercicioDao.getAllEjercicios()
}
package com.example.navigation.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.navigation.data.local.dao.EntrenamientoDao
import com.example.navigation.data.local.entities.toEntrenamiento
import com.example.navigation.domain.model.Entrenamiento
import jakarta.inject.Inject

class EntrenamientoRepository @Inject constructor(
    private val entrenamientoDao : EntrenamientoDao
){
    suspend fun getEntrenamientos(): List<Entrenamiento> {
        return entrenamientoDao.getAll().map { it.toEntrenamiento() }
    }

    fun getEntrenamientosLiveData(): LiveData<List<Entrenamiento>> {
        return entrenamientoDao.getAllLive().map { list ->
            list.map { it.toEntrenamiento() }
        }
    }
}
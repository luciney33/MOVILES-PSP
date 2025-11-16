package com.example.navigation.data.repository

import android.util.Log
import com.example.navigation.data.local.dao.EntrenamientoEjercicioDao
import com.example.navigation.data.local.dao.SesionDao
import com.example.navigation.data.local.dao.SesionEjercicioDao
import com.example.navigation.data.local.entity.SesionEjercicioEntity
import com.example.navigation.data.local.entity.SesionEntity
import com.example.navigation.data.local.entity.mappers.toDomainSesion
import com.example.navigation.data.local.entity.mappers.toDomainSesionConEjercicios
import com.example.navigation.data.local.entity.mappers.toDomainSesionEjercicio
import com.example.navigation.data.local.entity.mappers.toEntitySesionEjercicio
import com.example.navigation.domain.model.Sesion
import com.example.navigation.domain.model.SesionConEjercicios
import com.example.navigation.domain.model.SesionEjercicio
import javax.inject.Inject


class SesionRepositoryImpl @Inject constructor(
    private val sesionDao: SesionDao,
    private val sesionEjercicioDao: SesionEjercicioDao,
    private val entrenamientoEjercicioDao: EntrenamientoEjercicioDao
) {

    suspend fun getSesionConEjercicios(sesionId: Int): SesionConEjercicios? {
        // Obtener la sesión
        val sesionEntity: SesionEntity? = sesionDao.getById(sesionId)
        val entrenamientoId = sesionEntity?.entrenamientoId ?: 0

        val ejerciciosConName = sesionEjercicioDao.getBySesionIdConNameAndRelation(sesionId, entrenamientoId)


        return toDomainSesionConEjercicios(sesionEntity, ejerciciosConName)
    }

    suspend fun insertSesion(sesion: Sesion): Long {
        val entity = SesionEntity(
            id = sesion.id,
            entrenamientoId = sesion.entrenamientoId,
            fechaInicio = sesion.fechaInicio,
            duracionMs = sesion.duracionMs,
            ejerciciosCompletados = sesion.ejerciciosCompletados,
            notas = sesion.notas
        )
        return sesionDao.insert(entity)
    }

    suspend fun insertSesionEjercicio(sesionEjercicio: SesionEjercicio): Long {
        val entity = SesionEjercicioEntity(
            id = sesionEjercicio.id,
            sesionId = sesionEjercicio.sesionId,
            ejercicioId = sesionEjercicio.ejercicioId,
            orden = sesionEjercicio.orden,
            volumenKg = sesionEjercicio.volumenKg,
            notas = sesionEjercicio.notas
        )
        return sesionEjercicioDao.insert(entity)
    }

    // UPDATE para ejercicio de sesión
    suspend fun updateSesionEjercicio(domain: SesionEjercicio): Int {
        val entity = toEntitySesionEjercicio(domain)
        return sesionEjercicioDao.update(entity)

    }

    // Obtener ejercicio de sesión por id (con nombre)
    suspend fun getSesionEjercicioById(id: Int): SesionEjercicio? {
        // Si id corresponde a sesion_ejercicios, necesitaríamos el entrenamientoId para traer series;
        // intentamos obtener la entidad de sesion_ejercicios y su sesion para obtener entrenamientoId
        val seEntity = sesionEjercicioDao.getById(id)
        if (seEntity != null) {
            val sesion = sesionDao.getById(seEntity.sesionId)
            val entrenamientoId = sesion?.entrenamientoId ?: 0
            val conName = sesionEjercicioDao.getByIdConName(id, entrenamientoId)
            if (conName != null) {
                Log.d("SesionRepo", "getSesionEjercicioById: encontrado por se.id=${conName.id}")
                return toDomainSesionEjercicio(conName)
            }
        }

        // Fallback: buscar por ejercicioId (por si se pasó el id del ejercicio en lugar del id de sesion_ejercicio)
        Log.w("SesionRepo", "getSesionEjercicioById: no encontrado por id=$id, probando por ejercicioId")
        val byEj = sesionEjercicioDao.getFirstByEjercicioIdConName(id)
        if (byEj != null) {
            Log.d("SesionRepo", "getSesionEjercicioById: encontrado por ejercicioId=${byEj.id}")
            return toDomainSesionEjercicio(byEj)
        }

        Log.w("SesionRepo", "getSesionEjercicioById: tampoco encontrado por ejercicioId=$id")
        return null
    }

    // Métodos auxiliares que pueden usarse desde usecases/ViewModels
    suspend fun getSesionById(id: Int): Sesion? {
        val sesionEntity = sesionDao.getById(id)
        return toDomainSesion(sesionEntity)
    }

    suspend fun getEjerciciosBySesionId(sesionId: Int): List<SesionEjercicio> {
        val sesionEntity = sesionDao.getById(sesionId)
        val entrenamientoId = sesionEntity?.entrenamientoId ?: 0
        val list = sesionEjercicioDao.getBySesionIdConNameAndRelation(sesionId, entrenamientoId)

        return list.map { toDomainSesionEjercicio(it) }
    }

    // Actualizar series/repeticiones en la tabla relacion
    suspend fun updateRelationSeries(entrenamientoId: Int, ejercicioId: Int, series: Int, repeticiones: Int): Int {
        return entrenamientoEjercicioDao.updateRelation(entrenamientoId, ejercicioId, series, repeticiones)

    }

    suspend fun updateSesionDuracion(sesionId: Int, duracionMs: Long): Int {
        return sesionDao.updateDuracion(sesionId, duracionMs)

    }

}
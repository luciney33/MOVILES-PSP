package com.example.navigation.data.repository

import com.example.navigation.data.local.dao.EntrenamientoEjercicioDao
import com.example.navigation.data.local.dao.EntrenamientoDao
import com.example.navigation.data.local.dao.SesionDao
import com.example.navigation.data.local.dao.SesionEjercicioDao
import com.example.navigation.data.local.entity.mappers.toDomainSesion
import com.example.navigation.data.local.entity.mappers.toDomainSesionEjercicio
import com.example.navigation.data.local.entity.mappers.toEntitySesionEjercicio
import com.example.navigation.data.local.entity.mappers.toDomainSesionSummary
import com.example.navigation.domain.model.Sesion
import com.example.navigation.domain.model.SesionEjercicio
import com.example.navigation.domain.model.SesionSummary
import javax.inject.Inject


class SesionRepositoryImpl @Inject constructor(
    private val sesionDao: SesionDao,
    private val sesionEjercicioDao: SesionEjercicioDao,
    private val entrenamientoEjercicioDao: EntrenamientoEjercicioDao,
    private val entrenamientoDao: EntrenamientoDao
) {



    suspend fun updateSesionEjercicio(domain: SesionEjercicio): Int {
        val entity = toEntitySesionEjercicio(domain)
        return sesionEjercicioDao.update(entity)

    }

    suspend fun getSesionEjercicioById(id: Int): SesionEjercicio? {

        val seEntity = sesionEjercicioDao.getById(id)
        if (seEntity != null) {
            val sesion = sesionDao.getById(seEntity.sesionId)
            val entrenamientoId = sesion?.entrenamientoId ?: 0
            val conName = sesionEjercicioDao.getByIdConName(id, entrenamientoId)
            if (conName != null) {
                return toDomainSesionEjercicio(conName)
            }
        }

        val byEj = sesionEjercicioDao.getFirstByEjercicioIdConName(id)
        if (byEj != null) {
            return toDomainSesionEjercicio(byEj)
        }
        return null
    }

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

    suspend fun updateRelationSeries(entrenamientoId: Int, ejercicioId: Int, series: Int, repeticiones: Int): Int {
        return entrenamientoEjercicioDao.updateRelation(entrenamientoId, ejercicioId, series, repeticiones)

    }

    suspend fun updateSesionDuracion(sesionId: Int, duracionMs: Long): Int {
        return sesionDao.updateDuracion(sesionId, duracionMs)

    }

    suspend fun getAllSesionSummaries(): List<SesionSummary> {
        val sesiones = sesionDao.getAll()
        return sesiones.map { s ->
            val entrenamientoName = entrenamientoDao.getEntrenamientoById(s.entrenamientoId).nombre

            val ejercicios = sesionEjercicioDao.getBySesionId(s.id)
            val volumenTotal = ejercicios.sumOf { it.volumenKg }
            val totalFromEntrenamiento =  entrenamientoEjercicioDao.countByEntrenamientoId(s.entrenamientoId)
            val totalEjercicios = if (totalFromEntrenamiento > 0) totalFromEntrenamiento else ejercicios.size

            toDomainSesionSummary(s, entrenamientoName, volumenTotal, totalEjercicios)
        }
    }

    suspend fun getEntrenamientoName(entrenamientoId: Int): String {
        val ent = entrenamientoDao.getEntrenamientoById(entrenamientoId)
        return ent.nombre
    }

}
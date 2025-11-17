package com.example.navigation.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.navigation.data.local.entity.SesionEjercicioEntity
import com.example.navigation.data.local.entity.SesionEjercicioConName

@Dao
interface SesionEjercicioDao {

    @Insert
    suspend fun insert(sesionEjercicio: SesionEjercicioEntity): Long

    @Update
    suspend fun update(sesionEjercicio: SesionEjercicioEntity): Int

    @Delete
    suspend fun delete(sesionEjercicio: SesionEjercicioEntity)

    @Query("SELECT * FROM sesion_ejercicios WHERE sesionId = :sesionId ORDER BY orden ASC")
    suspend fun getBySesionId(sesionId: Int): List<SesionEjercicioEntity>

    @Query("SELECT se.id, se.sesionId, se.ejercicioId, e.nombre as nombreEjercicio, se.orden, se.volumenKg, se.notas, IFNULL(ee.series, 0) as series, IFNULL(ee.repeticiones, 0) as repeticiones FROM sesion_ejercicios se LEFT JOIN ejercicios e ON se.ejercicioId = e.id LEFT JOIN entrenamiento_ejercicio ee ON ee.ejercicioId = se.ejercicioId AND ee.entrenamientoId = :entrenamientoId WHERE se.sesionId = :sesionId ORDER BY se.orden ASC")
    suspend fun getBySesionIdConNameAndRelation(sesionId: Int, entrenamientoId: Int): List<SesionEjercicioConName>

    @Query("SELECT se.id, se.sesionId, se.ejercicioId, e.nombre as nombreEjercicio, se.orden, se.volumenKg, se.notas, IFNULL(ee.series, 0) as series, IFNULL(ee.repeticiones, 0) as repeticiones FROM sesion_ejercicios se JOIN ejercicios e ON se.ejercicioId = e.id LEFT JOIN entrenamiento_ejercicio ee ON ee.ejercicioId = se.ejercicioId AND ee.entrenamientoId = :entrenamientoId WHERE se.id = :id LIMIT 1")
    suspend fun getByIdConName(id: Int, entrenamientoId: Int): SesionEjercicioConName?

    @Query("SELECT * FROM sesion_ejercicios WHERE id = :id")
    suspend fun getById(id: Int): SesionEjercicioEntity?

    @Query("SELECT se.id, se.sesionId, se.ejercicioId, e.nombre as nombreEjercicio, se.orden, se.volumenKg, se.notas, IFNULL(ee.series, 0) as series, IFNULL(ee.repeticiones, 0) as repeticiones FROM sesion_ejercicios se JOIN ejercicios e ON se.ejercicioId = e.id LEFT JOIN entrenamiento_ejercicio ee ON ee.ejercicioId = se.ejercicioId WHERE se.ejercicioId = :ejercicioId ORDER BY se.id DESC LIMIT 1")
    suspend fun getLastByEjercicioIdConName(ejercicioId: Int): SesionEjercicioConName?

    @Query("SELECT se.id, se.sesionId, se.ejercicioId, e.nombre as nombreEjercicio, se.orden, se.volumenKg, se.notas, IFNULL(ee.series, 0) as series, IFNULL(ee.repeticiones, 0) as repeticiones FROM sesion_ejercicios se JOIN ejercicios e ON se.ejercicioId = e.id LEFT JOIN entrenamiento_ejercicio ee ON ee.ejercicioId = se.ejercicioId WHERE se.ejercicioId = :ejercicioId ORDER BY se.id DESC LIMIT 2")
    suspend fun getTop2ByEjercicioIdConName(ejercicioId: Int): List<SesionEjercicioConName>

    @Query("SELECT MAX(se.volumenKg) FROM sesion_ejercicios se WHERE se.ejercicioId = :ejercicioId")
    suspend fun getMaxVolumenByEjercicioId(ejercicioId: Int): Double?

    @Query("SELECT se.id, se.sesionId, se.ejercicioId, e.nombre as nombreEjercicio, se.orden, se.volumenKg, se.notas, IFNULL(ee.series, 0) as series, IFNULL(ee.repeticiones, 0) as repeticiones FROM sesion_ejercicios se JOIN ejercicios e ON se.ejercicioId = e.id LEFT JOIN entrenamiento_ejercicio ee ON ee.ejercicioId = se.ejercicioId WHERE se.ejercicioId = :ejercicioId LIMIT 1")
    suspend fun getFirstByEjercicioIdConName(ejercicioId: Int): SesionEjercicioConName?

}
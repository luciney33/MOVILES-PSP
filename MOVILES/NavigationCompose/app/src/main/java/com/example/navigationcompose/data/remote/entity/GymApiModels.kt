package com.example.navigationcompose.data.remote.entity

import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.domain.model.Ejercicio
import com.example.navigationcompose.domain.model.Entrenamiento
import com.example.navigationcompose.domain.model.Usuario

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val usuario: UsuarioEntity,
    val message: String,
    val requires2FA: Boolean
)
data class LoginRequest(val username: String, val password: String)
data class EjercicioEntity(
    val id: Long,
    val nombre: String,
    val tipoEntrenamiento: String,
    val imagenUrl: String,
    val descripcion: String
)

data class EntrenamientoEntity(
    val id: Long,
    val usuarioId: Long,
    val nombre: String,
    val descripcion: String,
    val ejercicios: List<EjercicioEntity>? = emptyList()
)


data class UsuarioEntity(
    val id: Long,
    val username: String,
    val email: String,
    val nombre: String,
    val password: String,
    val rol: String = Constantes.USER,
    val activo: Boolean = true,
    val publicKey: String? = null,
    val certificado: String? = null
)

fun EjercicioEntity.toDomain() = Ejercicio(
    id = id,
    nombre = nombre,
    tipo = tipoEntrenamiento,
    imageUrl = imagenUrl,
    descripcion = descripcion
)

fun Ejercicio.toEntity() = EjercicioEntity(
    id = id,
    nombre = nombre,
    tipoEntrenamiento = tipo,
    imagenUrl = imageUrl,
    descripcion = descripcion
)

fun EntrenamientoEntity.toDomain() = Entrenamiento(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    ejercicios = ejercicios?.map { it.toDomain() } ?: emptyList()
)

fun Entrenamiento.toEntity(usuarioId: Long) = EntrenamientoEntity(
    id = id,
    usuarioId = usuarioId,
    nombre = nombre,
    descripcion = descripcion,
    ejercicios = ejercicios.map { it.toEntity() }
)

fun UsuarioEntity.toDomain() = Usuario(
    id = id,
    username = username,
    email = email,
    nombre = nombre,
    rol = rol,
    publicKey = publicKey?.let { android.util.Base64.decode(it, android.util.Base64.DEFAULT) },
    certificado = certificado?.let { android.util.Base64.decode(it, android.util.Base64.DEFAULT) },
    certificadoVerificado = false
)
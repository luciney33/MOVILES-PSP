package com.example.navigationcompose.domain.model

/**
 * Modelos de dominio para el sistema de secretos.
 * Reutiliza Usuario de GymModel.kt para evitar duplicación.
 */

/**
 * Representa un secreto en la capa de dominio.
 */
data class Secreto(
    val id: Long,
    val autor: Usuario,
    val contenidoDescifrado: String?, // null hasta que se descifre
    val fechaCreacion: String,
    val compartidoCon: List<Usuario>,
    val esAutor: Boolean, // true si el usuario actual es el autor
    val firmaValida: Boolean? // null hasta que se verifique, true/false después
)

/**
 * Datos necesarios para crear un secreto.
 */
data class CrearSecretoData(
    val contenidoPlano: String,
    val receptorId: Long,
    val passwordUsuario: String // Para descifrar la clave privada del autor
)

/**
 * Resultado de descifrar un secreto.
 */
data class SecretoDescifrado(
    val secreto: Secreto,
    val contenidoPlano: String,
    val firmaValida: Boolean
)


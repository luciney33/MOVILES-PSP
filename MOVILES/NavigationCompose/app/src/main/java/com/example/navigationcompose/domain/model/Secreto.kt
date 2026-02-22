package com.example.navigationcompose.domain.model

data class Secreto(
    val id: Long,
    val autor: Usuario,
    val contenidoDescifrado: String?,
    val fechaCreacion: String,
    val compartidoCon: List<Usuario>,
    val esAutor: Boolean,
    val firmaValida: Boolean?
)

data class CrearSecretoData(
    val contenidoPlano: String,
    val receptorId: Long,
    val passwordUsuario: String
)

data class SecretoDescifrado(
    val secreto: Secreto,
    val contenidoPlano: String,
    val firmaValida: Boolean
)


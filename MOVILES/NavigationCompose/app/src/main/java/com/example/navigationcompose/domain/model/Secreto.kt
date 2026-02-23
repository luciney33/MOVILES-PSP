package com.example.navigationcompose.domain.model

data class Secreto(
    val id: Long,
    val autor: Usuario,
    val contenidoDescifrado: String?,
    val compartidoCon: List<Usuario>,
    val esAutor: Boolean,
    val firmaValida: Boolean?
)

data class SecretoDescifrado(
    val secreto: Secreto,
    val contenidoPlano: String,
    val firmaValida: Boolean
)


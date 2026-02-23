package com.example.navigationcompose.data.remote.entity

import com.example.navigationcompose.common.Constantes
import com.google.gson.annotations.SerializedName

data class CrearSecretoRequest(

    @SerializedName("contenidoCifrado")
    val contenidoCifrado: String,

    @SerializedName("claveAESCifrada")
    val claveAESCifrada: String,

    @SerializedName("firma")
    val firma: String,

    @SerializedName("iv")
    val iv: String
)

data class CompartirSecretoRequest(
    @SerializedName("receptorId")
    val receptorId: Long,

    @SerializedName("claveAESCifradaDestinatario")
    val claveAESCifradaDestinatario: String
)

data class SecretoSummaryDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("autorId")
    val autorId: Long,

    @SerializedName("autorUsername")
    val autorUsername: String,

    @SerializedName("autorNombre")
    val autorNombre: String,

    @SerializedName("esAutor")
    val esAutor: Boolean,

    @SerializedName("cantidadCompartidos")
    val cantidadCompartidos: Int
)

data class SecretoCifradoResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("autorId")
    val autorId: Long,

    @SerializedName("autorUsername")
    val autorUsername: String,

    @SerializedName("autorNombre")
    val autorNombre: String,

    @SerializedName("contenidoCifrado")
    val contenidoCifrado: String,

    @SerializedName("claveAESCifrada")
    val claveAESCifrada: String,

    @SerializedName("firma")
    val firma: String,

    @SerializedName("iv")
    val iv: String,

    @SerializedName("publicKeyAutor")
    val autorClavePublica: String,

    @SerializedName("esCompartido")
    val esCompartido: Boolean,

    @SerializedName("compartidoCon")
    val compartidoCon: List<UsuarioSecretoDto> = emptyList()
)

data class UsuarioSecretoDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("username")
    val username: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("publicKey")
    val publicKey: String,

    @SerializedName("certificado")
    val certificado: String
)


data class ClavePublicaServidorResponse(
    @SerializedName("publicKey")
    val publicKey: String
)


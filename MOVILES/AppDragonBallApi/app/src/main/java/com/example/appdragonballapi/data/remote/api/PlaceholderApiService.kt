package com.example.appdragonballapi.data.remote.api

import com.example.appdragonballapi.common.Constantes
import com.example.appdragonballapi.data.remote.entity.CharacterEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface PlaceholderApiService {

    @POST(Constantes.URL_PLACEHOLDER_POST)
    suspend fun addCharacter(@Body character: CharacterEntity): Response<CharacterEntity>

    @PUT(Constantes.URL_PLACEHOLDER_UPDATE)
    suspend fun updateCharacter(@Path(Constantes.ID) id: Int, @Body character: CharacterEntity): Response<CharacterEntity>

    @DELETE(Constantes.URL_PLACEHOLDER_DELETE)
    suspend fun delCharacter(@Path(Constantes.ID) id: Int): Response<Unit>
}
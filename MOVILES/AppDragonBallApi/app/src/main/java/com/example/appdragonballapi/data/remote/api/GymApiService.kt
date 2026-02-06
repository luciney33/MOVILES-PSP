package com.example.appdragonballapi.data.remote.api

import com.example.appdragonballapi.common.Constantes
import com.example.appdragonballapi.data.remote.entity.CharacterEntity
import com.example.appdragonballapi.data.remote.entity.DragonBallResponse
import com.example.appdragonballapi.data.remote.entity.PlanetaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GymApiService {
    @GET(Constantes.URL_CHARACTERS)
    suspend fun getCharacters(@Query(Constantes.PAGE) page: Int = 1): Response<DragonBallResponse>

    @GET(Constantes.URL_CHARACTERS_ID)
    suspend fun getCharacter(@Path(Constantes.ID) id: Int): Response<CharacterEntity>


    @GET(Constantes.URL_PLANETS)
    suspend fun getPlanets(@Query(Constantes.PAGE) page: Int = 1) : Response<PlanetaResponse>
}


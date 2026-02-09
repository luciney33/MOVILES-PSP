package com.example.navigationcompose.data.remote.api


import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.data.remote.entity.DragonBallResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DragonBallApiService {
    @GET(Constantes.URL_CHARACTERS)
    suspend fun getCharacters(@Query(Constantes.PAGE) page: Int = 1): Response<DragonBallResponse>

}


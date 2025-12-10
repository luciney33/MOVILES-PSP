package com.example.appdragonballapi.data.remote.api

import com.example.appdragonballapi.domain.model.DragonBallResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface DragonBallApiService {

    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int = 1): DragonBallResponse

    @GET("character")
    suspend fun searchCharacters(@Query("name") name: String, @Query("page") page: Int = 1): Response<DragonBallResponse>


}


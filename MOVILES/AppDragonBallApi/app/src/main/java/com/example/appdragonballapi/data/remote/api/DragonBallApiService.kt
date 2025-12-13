package com.example.appdragonballapi.data.remote.api

import com.example.appdragonballapi.data.remote.entity.CharacterEntity
import com.example.appdragonballapi.data.remote.entity.DragonBallResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface DragonBallApiService {
    @GET("characters")
    suspend fun getCharacters(@Query("page") page: Int = 1, @Query("name") name: String? = null): Response<DragonBallResponse>


    @GET("characters/{id}")
    suspend fun getCharacter(@Path("id") id: Int): Response<CharacterEntity>



    @POST("characters")
    suspend fun addCharacter(@Body character: CharacterEntity): Response<CharacterEntity>


    @PUT("characters/{id}")
    suspend fun updateCharacter(@Path("id") id: Int, @Body character: CharacterEntity): Response<CharacterEntity>


    @DELETE("characters/{id}")
    suspend fun delCharacter(@Path("id") id: Int): Response<Unit>

}


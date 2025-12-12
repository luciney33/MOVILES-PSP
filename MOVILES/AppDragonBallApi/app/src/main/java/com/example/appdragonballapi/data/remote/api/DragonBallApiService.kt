package com.example.appdragonballapi.data.remote.api

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
    suspend fun getCharacters(@Query("page") page: Int = 1): Response<DragonBallResponse>

    @GET("characters")
    suspend fun searchCharacters(@Query("name") name: String, @Query("page") page: Int = 1): Response<DragonBallResponse>

        @GET("/characters/{id}")
    suspend fun getCharacter(@Path("id") id : Int) : Response<DragonBallResponse>

    @POST("/characters")
    suspend fun postCharacter(@Body character: DragonBallResponse) : Response<DragonBallResponse>

    @PUT("/characters/{id}")
    suspend fun putCharacter(@Path("id") id : Int,@Body user: DragonBallResponse) : Response<DragonBallResponse>


    @DELETE("/characters/{id}")
    suspend fun delCharacter(@Path("id") id : Int) : Response<Unit>


}


package com.example.appdragonballapi.data

import com.example.appdragonballapi.data.remote.api.DragonBallApiService
import com.example.appdragonballapi.domain.model.DragonBallCharacter
import com.example.navigationhiltroom.common.NetworkResult
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DragonBallRepository @Inject constructor(
    private val apiService: DragonBallApiService
) {

    suspend fun getCharacters(page: Int = 1): NetworkResult<List<DragonBallCharacter>> {
        try {
            val response = apiService.getCharacters(page)
            return NetworkResult.Success(response.results)
        } catch (e: HttpException) {
            return NetworkResult.Error("Error ${e.code()} }")
        }
    }

    suspend fun searchCharacters(name: String, page: Int = 1): List<DragonBallCharacter> {
        try {

            val response = apiService.searchCharacters(name, page)

            if (response.isSuccessful) {
                return (response.body()?.results ?: emptyList())
            } else {
                response.errorBody() // esto se parsearia
                response.code()
                response.message()
                return emptyList();
            }
        } catch (e: Exception) {

            return emptyList()
        }

    }
}
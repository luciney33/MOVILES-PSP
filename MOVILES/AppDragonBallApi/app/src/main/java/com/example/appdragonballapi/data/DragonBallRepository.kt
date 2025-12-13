package com.example.appdragonballapi.data

import com.example.appdragonballapi.common.Constantes
import com.example.appdragonballapi.data.remote.api.DragonBallApiService
import com.example.appdragonballapi.data.remote.entity.toDomain
import com.example.appdragonballapi.domain.model.DragonBallCharacter
import com.example.navigationhiltroom.common.NetworkResult
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DragonBallRepository @Inject constructor(
    private val apiService: DragonBallApiService
) {

    suspend fun getCharacters(page: Int = 1, name: String?): NetworkResult<List<DragonBallCharacter>> {
        return try {
            val response = apiService.getCharacters(page, name)

            if (response.isSuccessful) {
                val dragonBallResponse = response.body()
                val domainList = dragonBallResponse?.characterEntities?.map { it.toDomain() } ?: emptyList()
                NetworkResult.Success(domainList)

            } else {
                NetworkResult.Error("${Constantes.ERROR_DEL_SERVIDOR}${response.code()} ${response.message()}")
            }

        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_DE_CONEXION}${e.message}")
        }
    }

    suspend fun getCharacterById(id: Int): NetworkResult<DragonBallCharacter> {
        return try {
            val response = apiService.getCharacter(id)

            if (response.isSuccessful) {
                val entity = response.body()

                if (entity != null) {
                    NetworkResult.Success(entity.toDomain())
                } else {
                    NetworkResult.Error(Constantes.EL_PERSONAJE_VINO_VACIO)
                }
            } else {
                NetworkResult.Error("${Constantes.ERROR_OBTENER_DETALLE} ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_DE_CONEXION}${e.message}")
        }

    }
}
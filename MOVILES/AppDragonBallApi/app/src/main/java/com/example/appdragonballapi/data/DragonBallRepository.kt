package com.example.appdragonballapi.data

import com.example.appdragonballapi.common.Constantes
import com.example.appdragonballapi.data.remote.api.DragonBallApiService
import com.example.appdragonballapi.data.remote.entity.toDomain
import com.example.appdragonballapi.data.remote.entity.toEntity
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

    suspend fun deleteCharacter(id: Int): NetworkResult<Unit> {
        return try {
            val response = apiService.delCharacter(id)

            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("${Constantes.ERROR_ELIMINAR_PERSONAJE}${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_DE_CONEXION}${e.message}")
        }
    }

    suspend fun updateCharacter(id: Int, character: DragonBallCharacter): NetworkResult<DragonBallCharacter> {
        return try {
            val characterEntity = character.toEntity()
            val response = apiService.updateCharacter(id, characterEntity)

            if (response.isSuccessful) {
                val updatedEntity = response.body()
                if (updatedEntity != null) {
                    NetworkResult.Success(updatedEntity.toDomain())
                } else {
                    NetworkResult.Error(Constantes.EL_PERSONAJE_VINO_VACIO)
                }
            } else {
                NetworkResult.Error("${Constantes.ERROR_ACTUALIZAR_PERSONAJE}${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_DE_CONEXION}${e.message}")
        }
    }

    suspend fun addCharacter(character: DragonBallCharacter): NetworkResult<DragonBallCharacter> {
        return try {
            val characterEntity = character.toEntity()
            val response = apiService.addCharacter(characterEntity)

            if (response.isSuccessful) {
                val newEntity = response.body()
                if (newEntity != null) {
                    NetworkResult.Success(newEntity.toDomain())
                } else {
                    NetworkResult.Error(Constantes.EL_PERSONAJE_VINO_VACIO)
                }
            } else {
                NetworkResult.Error("${Constantes.ERROR_AGREGAR_PERSONAJE}${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_DE_CONEXION}${e.message}")
        }
    }
}
package com.example.navigationhiltroom.domain.usecase

import com.example.appdragonballapi.data.DragonBallRepository
import com.example.appdragonballapi.domain.model.DragonBallCharacter
import com.example.navigationhiltroom.common.NetworkResult
import jakarta.inject.Inject

class GetDragonBallCharacters @Inject constructor(private val repository: DragonBallRepository) {

//    operator suspend fun invoke(page: Int): NetworkResult<List<DragonBallCharacter>> =
////        isValidPage(page)
////            .then  { isPageMayorCero(page) }
////            .then { repository.getCharacters(page)
//            }


}

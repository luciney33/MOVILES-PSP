package com.example.navigationhiltroom.common


sealed class NetworkResult<T>(

) {

    class Success<T>(val data: T) : NetworkResult<T>()

    class Error<T>(val message: String) : NetworkResult<T>()


    inline fun <R> map( transform :(data: T) -> R) : NetworkResult<R> =
        when(this){
            is Error -> Error(message)
            is Success -> Success(transform(data))
        }
    // Encadena transformaciones que retornan NetworkResult
    inline fun <R> then(transform: (data: T) -> NetworkResult<R>): NetworkResult<R> =
        when (this) {
            is Error -> Error(message)
            is Success -> transform(data)
        }




}

// Combina una lista de NetworkResult en un único NetworkResult de lista
fun <T> List<NetworkResult<T>>.combine(): NetworkResult<List<T>> {
    val successData = mutableListOf<T>()

    for (result in this) {
        when (result) {
            is NetworkResult.Success -> successData.add(result.data)
            is NetworkResult.Error -> return NetworkResult.Error(result.message)
        }
    }
    return NetworkResult.Success(successData)
}


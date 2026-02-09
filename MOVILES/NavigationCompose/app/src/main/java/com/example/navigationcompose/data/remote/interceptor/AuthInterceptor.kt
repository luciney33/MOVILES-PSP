package com.example.navigationcompose.data.remote.interceptor

import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.url.encodedPath.contains(Constantes.API_AUTH_LOGIN)) {
            return chain.proceed(request)
        }

        val token = tokenManager.getAccessToken()
        val newRequestBuilder = request.newBuilder()

        if (!token.isNullOrBlank()) {
            newRequestBuilder.header(Constantes.AUTH_HEADER, "${Constantes.AUTH_BEARER_PREFIX}$token")
        }

        return chain.proceed(newRequestBuilder.build())
    }
}
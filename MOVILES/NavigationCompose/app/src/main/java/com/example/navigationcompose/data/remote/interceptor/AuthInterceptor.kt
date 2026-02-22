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
        val path = request.url.encodedPath

        // No agregar token a endpoints públicos
        if (path.contains(Constantes.API_AUTH_LOGIN) ||
            path.contains("/api/auth/register") ||
            path.contains("/api/auth/activar")) {
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
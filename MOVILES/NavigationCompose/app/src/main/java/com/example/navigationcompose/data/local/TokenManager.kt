package com.example.navigationcompose.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.navigationcompose.common.Constantes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constantes.PREFS_NAME, Context.MODE_PRIVATE)

    fun saveAccessToken(token: String) {
        prefs.edit().putString(Constantes.PREF_ACCESS_TOKEN, token).apply()
    }

    fun getAccessToken(): String? {
        return prefs.getString(Constantes.PREF_ACCESS_TOKEN, null)
    }

    fun saveRefreshToken(token: String) {
        prefs.edit().putString(Constantes.PREF_REFRESH_TOKEN, token).apply()
    }

    fun clearTokens() {
        prefs.edit().clear().apply()
    }
}
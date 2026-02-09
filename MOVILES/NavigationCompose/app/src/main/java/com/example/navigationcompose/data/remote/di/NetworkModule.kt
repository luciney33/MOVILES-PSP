package com.example.navigationcompose.data.remote.di

import com.example.navigationcompose.data.remote.api.GymApiService
import com.example.navigationcompose.data.remote.interceptor.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor // Inyectamos el que acabamos de crear
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor) // <--- ESTO activa la seguridad en Android
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("GymRetrofit")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2") // Tu API de Spring
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGymApiService(@Named("GymRetrofit") retrofit: Retrofit): GymApiService {
        return retrofit.create(GymApiService::class.java)
    }

    // --- SECCIÓN API EXTERNA (Para la 3ª pestaña del BottomBar) ---
    @Provides
    @Singleton
    @Named("ExternalRetrofit")
    fun provideExternalRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dragonball-api.com") // API Externa de ejemplo
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}
package com.example.navigationcompose.data.remote.di

import com.example.navigationcompose.BuildConfig
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.data.remote.api.DragonBallApiService
import com.example.navigationcompose.data.remote.api.GymApiService
import com.example.navigationcompose.data.remote.api.SecretosApiService
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
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named(Constantes.RETROFIT_GYMAPI)
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGymApiService(@Named(Constantes.RETROFIT_GYMAPI) retrofit: Retrofit): GymApiService {
        return retrofit.create(GymApiService::class.java)
    }

    @Provides
    @Singleton
    @Named(Constantes.RETROFIT_SECRETOSAPI)
    fun provideSecretosRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSecretosApiService(@Named(Constantes.RETROFIT_SECRETOSAPI) retrofit: Retrofit): SecretosApiService {
        return retrofit.create(SecretosApiService::class.java)
    }


    @Provides
    @Singleton
    @Named(Constantes.RETROFIT_DBAPI)
    fun provideDragonBallRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_DRAGONBALL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideDragonBallApiService(@Named(Constantes.RETROFIT_DBAPI) retrofit: Retrofit): DragonBallApiService {
        return retrofit.create(DragonBallApiService::class.java)
    }

}
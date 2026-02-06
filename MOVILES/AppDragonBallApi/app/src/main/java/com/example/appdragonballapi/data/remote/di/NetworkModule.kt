package com.example.appdragonballapi.data.remote.di

import com.example.appdragonballapi.BuildConfig
import com.example.appdragonballapi.common.Constantes
import com.example.appdragonballapi.data.remote.api.GymApiService
import com.example.appdragonballapi.data.remote.api.PlaceholderApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.apply
import kotlin.jvm.java


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
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor
                            ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named(Constantes.RETROFIT_DBAPI)
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Provides
    @Singleton
    fun provideDragongBallApiService(@Named(Constantes.RETROFIT_DBAPI) retrofit: Retrofit): GymApiService {
        return retrofit.create(GymApiService::class.java)
    }

    @Provides
    @Singleton
    @Named(Constantes.RETROFIT_PLACEHOLDER)
    fun providePlaceholderRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_PLACEHOLDER)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePlaceholderApiService(@Named(Constantes.RETROFIT_PLACEHOLDER) retrofit: Retrofit): PlaceholderApiService {
        return retrofit.create(PlaceholderApiService::class.java)
    }
}

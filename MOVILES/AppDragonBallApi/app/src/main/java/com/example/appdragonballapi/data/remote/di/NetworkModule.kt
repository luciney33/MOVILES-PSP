package com.example.appdragonballapi.data.remote.di

import com.example.appdragonballapi.BuildConfig
import com.example.appdragonballapi.data.remote.api.DragonBallApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor,
                            ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            //.addInterceptor(AuthInterceptor(BuildConfig.API_KEY))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)  // Ahora usa BuildConfig
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())

//            .converterFactories {
//                add(GsonConverterFactory.create())
//            }

            .build()
    }

    @Provides
    @Singleton
    fun provideDragongBallApiService(retrofit: Retrofit): DragonBallApiService {
        return retrofit.create(DragonBallApiService::class.java)
    }
}

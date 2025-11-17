package com.example.navigation.data.local.di

import android.content.Context
import androidx.room.Room
import com.example.navigation.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DATABASE_NAME = "entrenamientosdb.db"


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
    DATABASE_NAME
        )
            .createFromAsset(DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideEntrenamiento(database: AppDatabase) = database.entrenamientoDao()

    @Provides
    fun provideEntrenamientoEjercicio(database: AppDatabase) = database.entrenamientoEjercicioDao()

    @Provides
    fun provideEjercicio(database: AppDatabase) = database.ejercicioDao()

    @Provides
    fun provideSesionDao(database: AppDatabase) = database.sesionDao()

    @Provides
    fun provideSesionEjercicioDao(database: AppDatabase) = database.sesionEjercicioDao()

    @Provides
    fun provideProgresoDao(database: AppDatabase) = database.progresoDao()

}
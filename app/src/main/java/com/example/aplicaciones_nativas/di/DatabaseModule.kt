package com.example.aplicaciones_nativas.di

import android.content.Context
import androidx.room.Room
import com.example.aplicaciones_nativas.data.AppDatabase
import com.example.aplicaciones_nativas.data.MIGRATION_1_2
import com.example.aplicaciones_nativas.data.MomentDao
import com.example.aplicaciones_nativas.data.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "social_app.db"
        ).addMigrations(MIGRATION_1_2).build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideMomentDao(database: AppDatabase): MomentDao {
        return database.momentDao()
    }
}

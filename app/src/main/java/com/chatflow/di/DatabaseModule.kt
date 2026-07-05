package com.chatflow.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.chatflow.data.local.db.AppDatabase
import com.chatflow.data.local.db.ApiKeyDao
import com.chatflow.data.repository.ApiKeyRepositoryImpl
import com.chatflow.domain.repository.ApiKeyRepository
import com.chatflow.data.local.secure.SecureStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "chatflow_db"
        ).build()
    }

    @Provides
    fun provideApiKeyDao(db: AppDatabase): ApiKeyDao {
        return db.apiKeyDao()
    }

    @Provides
    @Singleton
    fun provideApiKeyRepository(
        apiKeyDao: ApiKeyDao,
        secureStorage: SecureStorage
    ): ApiKeyRepository {
        return ApiKeyRepositoryImpl(apiKeyDao, secureStorage)
    }
}

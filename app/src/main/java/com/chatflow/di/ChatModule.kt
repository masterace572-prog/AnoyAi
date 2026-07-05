package com.chatflow.di

import android.content.Context
import com.chatflow.data.local.db.ChatDao
import com.chatflow.data.local.db.AppDatabase
import com.chatflow.data.repository.ChatRepositoryImpl
import com.chatflow.domain.repository.ChatRepository
import com.chatflow.domain.repository.AiProvider
import com.chatflow.data.remote.providers.GroqProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    fun provideChatDao(db: AppDatabase): ChatDao = db.chatDao()

    @Provides
    @Singleton
    fun provideChatRepository(chatDao: ChatDao): ChatRepository = ChatRepositoryImpl(chatDao)

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideAiProviders(groqProvider: GroqProvider): Map<String, AiProvider> {
        return mapOf(
            "groq" to groqProvider
        )
    }
}

package com.chatflow.di

import com.chatflow.data.repository.ModelRepositoryImpl
import com.chatflow.domain.repository.ModelRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModelModule {
    @Provides
    @Singleton
    fun provideModelRepository(impl: ModelRepositoryImpl): ModelRepository = impl
    
    @Provides
    @Singleton
    fun provideModelRepositoryImpl(providers: javax.inject.Provider<kotlin.collections.Map<String, com.chatflow.domain.repository.AiProvider>>): ModelRepositoryImpl {
        return ModelRepositoryImpl(providers)
    }
}

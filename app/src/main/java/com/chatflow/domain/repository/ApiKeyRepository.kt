package com.chatflow.domain.repository

import com.chatflow.domain.model.ApiKey
import kotlinx.coroutines.flow.Flow

interface ApiKeyRepository {
    fun getAllApiKeys(): Flow<List<ApiKey>>
    suspend fun saveApiKey(apiKey: ApiKey)
    suspend fun deleteApiKey(apiKey: ApiKey)
    suspend fun getApiKeyById(id: String): ApiKey?
}

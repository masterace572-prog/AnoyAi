package com.chatflow.domain.repository

import com.chatflow.domain.model.AiModel
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    fun getAvailableModels(providerId: String): Flow<List<AiModel>>
    suspend fun refreshModels(providerId: String, apiKey: String)
    suspend fun getAllModels(): List<AiModel>
}

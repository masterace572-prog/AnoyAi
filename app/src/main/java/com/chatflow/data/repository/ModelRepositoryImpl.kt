package com.chatflow.data.repository

import com.chatflow.domain.model.AiModel
import com.chatflow.domain.repository.AiProvider
import com.chatflow.domain.repository.ModelRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelRepositoryImpl @Inject constructor(
    private val providers: javax.inject.Provider<kotlin.collections.Map<String, AiProvider>>
) : ModelRepository {

    private val _modelsCache = MutableStateFlow<Map<String, List<AiModel>>>(emptyMap())

    override fun getAvailableModels(providerId: String): Flow<List<AiModel>> {
        return _modelsCache.map { it[providerId] ?: emptyList() }
    }

    override suspend fun refreshModels(providerId: String, apiKey: String) {
        val provider = providers.get()[providerId] ?: return
        val models = provider.fetchModels(apiKey)
        _modelsCache.update { current ->
            current + (providerId to models)
        }
    }

    override suspend fun getAllModels(): List<AiModel> {
        return _modelsCache.value.values.flatten()
    }
}

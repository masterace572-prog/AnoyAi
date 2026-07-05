package com.chatflow.data.repository

import com.chatflow.data.local.db.ApiKeyDao
import com.chatflow.data.local.db.ApiKeyEntity
import com.chatflow.data.local.secure.SecureStorage
import com.chatflow.domain.model.ApiKey
import com.chatflow.domain.repository.ApiKeyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ApiKeyRepositoryImpl @Inject constructor(
    private val apiKeyDao: ApiKeyDao,
    private val secureStorage: SecureStorage
) : ApiKeyRepository {

    override fun getAllApiKeys(): Flow<List<ApiKey>> {
        return apiKeyDao.getAllKeys().map { entities ->
            entities.map { entity ->
                ApiKey(
                    id = entity.id,
                    providerId = entity.providerId,
                    label = entity.label,
                    key = secureStorage.getKey(entity.id) ?: "",
                    isActive = entity.isActive
                )
            }
        }
    }

    override suspend fun saveApiKey(apiKey: ApiKey) {
        val id = if (apiKey.id.isEmpty()) UUID.randomUUID().toString() else apiKey.id
        
        // 1. Save metadata to Room
        val entity = ApiKeyEntity(
            id = id,
            providerId = apiKey.providerId,
            label = apiKey.label,
            encryptedKey = "encrypted", // Just a placeholder, the real key is in SecureStorage
            isActive = apiKey.isActive
        )
        apiKeyDao.insertKey(entity)

        // 2. Save actual key to EncryptedSharedPreferences
        secureStorage.saveKey(id, apiKey.key)
    }

    override suspend fun deleteApiKey(apiKey: ApiKey) {
        apiKeyDao.deleteKey(ApiKeyEntity(apiKey.id, apiKey.providerId, apiKey.label, "", apiKey.isActive))
        secureStorage.removeKey(apiKey.id)
    }

    override suspend fun getApiKeyById(id: String): ApiKey? {
        val entity = apiKeyDao.getKeyById(id) ?: return null
        return ApiKey(
            id = entity.id,
            providerId = entity.providerId,
            label = entity.label,
            key = secureStorage.getKey(id) ?: "",
            isActive = entity.isActive
        )
    }
}

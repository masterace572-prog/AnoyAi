package com.chatflow.domain.usecase

import com.chatflow.domain.model.ChatMessage
import com.chatflow.domain.model.ParsedChunk
import com.chatflow.domain.repository.AiProvider
import com.chatflow.domain.repository.ApiKeyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class StreamResponseUseCase @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val providers: javax.inject.Provider<kotlin.collections.Map<String, AiProvider>> 
) {
    // Note: using a Map of providers injected via Hilt
    
    fun execute(
        providerId: String,
        apiKeyId: String,
        modelId: String,
        messages: List<ChatMessage>,
        httpClient: OkHttpClient
    ): Flow<ParsedChunk> = flow {
        val apiKey = apiKeyRepository.getApiKeyById(apiKeyId) ?: throw Exception("API Key not found")
        val provider = providers.get()[providerId] ?: throw Exception("Provider not found")
        
        val request = provider.buildRequest(apiKey.key, modelId, messages, stream = true)
        
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("API Error: ${response.code}")
            
            val reader = BufferedReader(InputStreamReader(response.body?.byteStream()))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                line?.let {
                    provider.parseStreamChunk(it)?.let { chunk ->
                        emit(chunk)
                        if (chunk.isDone) return@flow
                    }
                }
            }
        }
    }
}

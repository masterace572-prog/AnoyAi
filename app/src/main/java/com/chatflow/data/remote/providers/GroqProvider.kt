package com.chatflow.data.remote.providers

import com.chatflow.data.remote.GroqApi
import com.chatflow.data.remote.dto.OpenAiChatRequest
import com.chatflow.data.remote.dto.OpenAiMessage
import com.chatflow.domain.model.AiModel
import com.chatflow.domain.model.ChatMessage
import com.chatflow.domain.model.ParsedChunk
import com.chatflow.domain.repository.AiProvider
import com.google.gson.Gson
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class GroqProvider @Inject constructor(
    private val gson: Gson
) : AiProvider {
    override val id: String = "groq"
    override val displayName: String = "Groq"

    override fun buildRequest(
        apiKey: String,
        model: String,
        messages: List<ChatMessage>,
        stream: Boolean
    ): Request {
        val openAiMessages = messages.map { 
            OpenAiMessage(
                role = it.role.name.lowercase(), 
                content = it.content 
            ) 
        }
        val requestBody = OpenAiChatRequest(
            model = model,
            messages = openAiMessages,
            stream = stream
        )
        
        val json = gson.toJson(requestBody)
        return Request.Builder()
            .url("https://api.groq.com/openai/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()
    }

    override fun parseStreamChunk(rawChunk: String): ParsedChunk? {
        if (!rawChunk.startsWith("data: ")) return null
        val data = rawChunk.removePrefix("data: ").trim()
        if (data == "[DONE]") return ParsedChunk(isDone = true)

        return try {
            // Simplified parsing for the MVP
            val response = gson.fromJson(data, com.chatflow.data.remote.dto.OpenAiChatResponse::class.java)
            val delta = response.choices.firstOrNull()?.delta
            ParsedChunk(
                contentDelta = delta?.content,
                reasoningDelta = delta?.reasoningContent
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun fetchModels(apiKey: String): List<AiModel> {
        // For MVP, we return a curated list. Real API call can be added later.
        return listOf(
            AiModel("llama-3.1-70b-versatile", id, "Llama 3.1 70B", isFree = true, supportsReasoning = false, contextLength = 128000),
            AiModel("llama3-8b-8192", id, "Llama 3 8B", isFree = true, supportsReasoning = false, contextLength = 8192),
            AiModel("deepseek-r1-distill-llama-70b", id, "DeepSeek R1 (Distill)", isFree = true, supportsReasoning = true, contextLength = 128000)
        )
    }
}

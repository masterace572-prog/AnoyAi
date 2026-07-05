package com.chatflow.domain.repository

import com.chatflow.domain.model.AiModel
import com.chatflow.domain.model.ChatMessage
import com.chatflow.domain.model.ParsedChunk
import okhttp3.Request
import okhttp3.Response

interface AiProvider {
    val id: String
    val displayName: String

    fun buildRequest(
        apiKey: String,
        model: String,
        messages: List<ChatMessage>,
        stream: Boolean
    ): Request

    fun parseStreamChunk(rawChunk: String): ParsedChunk?

    suspend fun fetchModels(apiKey: String): List<AiModel>
}

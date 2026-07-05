package com.chatflow.data.remote

import com.chatflow.data.remote.dto.OpenAiChatRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming
import okhttp3.ResponseBody

interface GroqApi {
    @Streaming
    @POST("openai/v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") token: String,
        @Body request: OpenAiChatRequest
    ): ResponseBody
}

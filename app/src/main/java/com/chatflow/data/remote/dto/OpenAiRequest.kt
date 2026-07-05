package com.chatflow.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OpenAiChatRequest(
    val model: String,
    val messages: List<OpenAiMessage>,
    val stream: Boolean
)

data class OpenAiMessage(
    val role: String,
    val content: String
)

data class OpenAiChatResponse(
    val choices: List<OpenAiChoice>
)

data class OpenAiChoice(
    val delta: OpenAiDelta
)

data class OpenAiDelta(
    val content: String? = null,
    @SerializedName("reasoning_content")
    val reasoningContent: String? = null
)

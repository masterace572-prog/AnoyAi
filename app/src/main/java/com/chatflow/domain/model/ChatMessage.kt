package com.chatflow.domain.model

data class ChatMessage(
    val role: MessageRole,
    val content: String,
    val reasoning: String? = null
)

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

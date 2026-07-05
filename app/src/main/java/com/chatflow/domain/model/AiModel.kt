package com.chatflow.domain.model

data class AiModel(
    val id: String,
    val providerId: String,
    val displayName: String,
    val isFree: Boolean,
    val supportsReasoning: Boolean,
    val contextLength: Int
)

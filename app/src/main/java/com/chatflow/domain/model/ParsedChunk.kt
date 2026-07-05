package com.chatflow.domain.model

data class ParsedChunk(
    val contentDelta: String? = null,
    val reasoningDelta: String? = null,
    val isDone: Boolean = false
)

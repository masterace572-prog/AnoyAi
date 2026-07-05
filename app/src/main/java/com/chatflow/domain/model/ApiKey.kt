package com.chatflow.domain.model

data class ApiKey(
    val id: String,
    val providerId: String,
    val label: String,
    val key: String,
    val isActive: Boolean = true
)

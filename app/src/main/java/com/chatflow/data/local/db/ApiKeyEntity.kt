package com.chatflow.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_keys")
data class ApiKeyEntity(
    @PrimaryKey val id: String,
    val providerId: String, // "gemini", "groq", "deepseek", "openrouter"
    val label: String,
    val encryptedKey: String, // This will be the key stored in EncryptedSharedPreferences, or a reference
    val isActive: Boolean = true,
    val addedAt: Long = System.currentTimeMillis()
)

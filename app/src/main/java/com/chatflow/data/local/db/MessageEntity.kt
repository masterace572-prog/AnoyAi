package com.chatflow.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val role: String,
    val content: String,
    val reasoning: String? = null,
    val modelUsed: String? = null,
    val timestamp: Long,
    val isError: Boolean = false
)

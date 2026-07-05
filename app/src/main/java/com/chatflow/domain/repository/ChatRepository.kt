package com.chatflow.domain.repository

import com.chatflow.data.local.db.ChatEntity
import com.chatflow.data.local.db.MessageEntity
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getAllChats(): Flow<List<ChatEntity>>
    suspend fun getChatById(chatId: String): ChatEntity?
    suspend fun saveChat(chat: ChatEntity)
    suspend fun deleteChat(chat: ChatEntity)
    suspend fun clearAllChats()
    
    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>
    suspend fun saveMessage(message: MessageEntity)
}

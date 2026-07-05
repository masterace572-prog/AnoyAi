package com.chatflow.data.repository

import com.chatflow.data.local.db.ChatDao
import com.chatflow.data.local.db.ChatEntity
import com.chatflow.data.local.db.MessageEntity
import com.chatflow.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao
) : ChatRepository {
    override fun getAllChats(): Flow<List<ChatEntity>> = chatDao.getAllChats()
    override suspend fun getChatById(chatId: String): ChatEntity? = chatDao.getChatById(chatId)
    override suspend fun saveChat(chat: ChatEntity) = chatDao.insertChat(chat)
    override suspend fun deleteChat(chat: ChatEntity) = chatDao.deleteChat(chat)
    override fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> = chatDao.getMessagesByChat(chatId)
    override suspend fun saveMessage(message: MessageEntity) = chatDao.insertMessage(message)
}

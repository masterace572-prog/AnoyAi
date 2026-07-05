package com.chatflow.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatflow.data.local.db.MessageEntity
import com.chatflow.domain.model.ChatMessage
import com.chatflow.domain.model.MessageRole
import com.chatflow.domain.model.ParsedChunk
import com.chatflow.domain.repository.ChatRepository
import com.chatflow.domain.usecase.StreamResponseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val streamResponseUseCase: StreamResponseUseCase,
    private val okHttpClient: OkHttpClient
) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages: StateFlow<List<MessageEntity>> = _messages

    // For simplicity in MVP, we use a fixed chat ID
    private val currentChatId = "default_chat"

    init {
        viewModelScope.launch {
            chatRepository.getMessagesForChat(currentChatId).collect {
                _messages.value = it
            }
        }
    }

    fun sendMessage(text: String, providerId: String, apiKeyId: String, modelId: String) {
        viewModelScope.launch {
            // 1. Save user message
            val userMsg = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = currentChatId,
                role = "user",
                content = text,
                timestamp = System.currentTimeMillis()
            )
            chatRepository.saveMessage(userMsg)

            // 2. Prepare context for AI
            val context = _messages.value.map { 
                ChatMessage(
                    role = if(it.role == "user") MessageRole.USER else MessageRole.ASSISTANT,
                    content = it.content
                )
            } + ChatMessage(MessageRole.USER, text)

            // 3. Stream response
            var accumulatedContent = ""
            var accumulatedReasoning = ""
            val assistantMsgId = UUID.randomUUID().toString()

            try {
                streamResponseUseCase.execute(providerId, apiKeyId, modelId, context, okHttpClient)
                    .collect { chunk ->
                        chunk.reasoningDelta?.let { accumulatedReasoning += it }
                        chunk.contentDelta?.let { accumulatedContent += it }
                        
                        // Update the UI in real-time
                        val currentList = _messages.value.toMutableList()
                        val index = currentList.indexOfFirst { it.id == assistantMsgId }
                        if (index == -1) {
                            currentList.add(MessageEntity(
                                id = assistantMsgId,
                                chatId = currentChatId,
                                role = "assistant",
                                content = accumulatedContent,
                                reasoning = accumulatedReasoning,
                                timestamp = System.currentTimeMillis()
                            ))
                        } else {
                            currentList[index] = currentList[index].copy(
                                content = accumulatedContent,
                                reasoning = accumulatedReasoning
                            )
                        }
                        _messages.value = currentList
                    }
                
                // 4. Final save to DB
                chatRepository.saveMessage(MessageEntity(
                    id = assistantMsgId,
                    chatId = currentChatId,
                    role = "assistant",
                    content = accumulatedContent,
                    reasoning = accumulatedReasoning,
                    timestamp = System.currentTimeMillis()
                ))
                
            } catch (e: Exception) {
                // Handle error message
            }
        }
    }
}

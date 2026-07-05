package com.chatflow.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatflow.data.local.db.MessageEntity
import com.chatflow.domain.model.AiModel
import com.chatflow.domain.model.ChatMessage
import com.chatflow.domain.model.MessageRole
import com.chatflow.domain.model.ParsedChunk
import com.chatflow.domain.repository.ChatRepository
import com.chatflow.domain.repository.ModelRepository
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
    private val modelRepository: ModelRepository,
    private val streamResponseUseCase: StreamResponseUseCase,
    private val okHttpClient: OkHttpClient
) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages: StateFlow<List<MessageEntity>> = _messages

    private val _selectedModel = MutableStateFlow<AiModel?>(null)
    val selectedModel: StateFlow<AiModel?> = _selectedModel

    private val _selectedApiKeyId = MutableStateFlow<String?>(null)
    val selectedApiKeyId: StateFlow<String?> = _selectedApiKeyId

    private val currentChatId = "default_chat"

    init {
        viewModelScope.launch {
            chatRepository.getMessagesForChat(currentChatId).collect {
                _messages.value = it
            }
        }
    }

    fun setModel(model: AiModel) {
        _selectedModel.value = model
    }

    fun setApiKey(apiKeyId: String) {
        _selectedApiKeyId.value = apiKeyId
    }

    fun sendMessage(text: String) {
        val model = _selectedModel.value ?: return
        val apiKeyId = _selectedApiKeyId.value ?: return
        
        viewModelScope.launch {
            val userMsg = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = currentChatId,
                role = "user",
                content = text,
                timestamp = System.currentTimeMillis()
            )
            chatRepository.saveMessage(userMsg)

            val context = _messages.value.map { 
                ChatMessage(
                    role = if(it.role == "user") MessageRole.USER else MessageRole.ASSISTANT,
                    content = it.content
                )
            } + ChatMessage(MessageRole.USER, text)

            var accumulatedContent = ""
            var accumulatedReasoning = ""
            val assistantMsgId = UUID.randomUUID().toString()

            try {
                streamResponseUseCase.execute(
                    providerId = model.providerId, 
                    apiKeyId = apiKeyId, 
                    modelId = model.id, 
                    messages = context, 
                    httpClient = okHttpClient
                ).collect { chunk ->
                    chunk.reasoningDelta?.let { accumulatedReasoning += it }
                    chunk.contentDelta?.let { accumulatedContent += it }
                    
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
                
                chatRepository.saveMessage(MessageEntity(
                    id = assistantMsgId,
                    chatId = currentChatId,
                    role = "assistant",
                    content = accumulatedContent,
                    reasoning = accumulatedReasoning,
                    timestamp = System.currentTimeMillis()
                ))
                
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

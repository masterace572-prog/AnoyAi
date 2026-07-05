package com.chatflow.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatflow.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    fun clearAllChats() {
        viewModelScope.launch {
            chatRepository.clearAllChats()
        }
    }
}

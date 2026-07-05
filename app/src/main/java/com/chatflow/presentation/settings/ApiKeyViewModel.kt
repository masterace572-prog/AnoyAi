package com.chatflow.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatflow.domain.model.ApiKey
import com.chatflow.domain.repository.ApiKeyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiKeyViewModel @Inject constructor(
    private val repository: ApiKeyRepository
) : ViewModel() {

    val apiKeys: StateFlow<List<ApiKey>> = repository.getAllApiKeys()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveApiKey(providerId: String, label: String, key: String) {
        viewModelScope.launch {
            val apiKey = ApiKey(
                id = "", // Repository will generate UUID
                providerId = providerId,
                label = label,
                key = key
            )
            repository.saveApiKey(apiKey)
        }
    }

    fun deleteApiKey(apiKey: ApiKey) {
        viewModelScope.launch {
            repository.deleteApiKey(apiKey)
        }
    }
}

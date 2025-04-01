package com.mindcare.mindcare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.mindcare.mindcare.data.AppDatabase
import com.mindcare.mindcare.data.UserInfo
import com.mindcare.mindcare.network.ApiClient
import com.mindcare.mindcare.network.ChatRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "mindcare-database"
    ).build()

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            _userInfo.value = db.userInfoDao().getUserInfo(1)
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.sendMessage(
                    ChatRequest(
                        message = message,
                        userContext = _userInfo.value?.toString() ?: ""
                    )
                )
                
                val newMessage = ChatMessage(
                    text = message,
                    isUser = true,
                    timestamp = System.currentTimeMillis()
                )
                
                val aiResponse = ChatMessage(
                    text = response.reply,
                    isUser = false,
                    timestamp = System.currentTimeMillis()
                )
                
                _chatHistory.value = _chatHistory.value + newMessage + aiResponse
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long
) 
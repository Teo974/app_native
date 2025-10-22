package com.buenosaires.connect.features.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val formatter = DateTimeFormatter.ofPattern("HH:mm")
    private val userAuthor = "Tú"

    private val _conversations = MutableStateFlow(initialConversations())
    private val conversations = _conversations.asStateFlow()

    val previews: StateFlow<List<ChatPreview>> = conversations
        .map { map ->
            map.entries.map { (contact, messages) ->
                val lastMessage = messages.lastOrNull()
                ChatPreview(
                    contact = contact,
                    lastMessage = lastMessage?.content ?: "",
                    lastTime = lastMessage?.time ?: "",
                    unreadCount = messages.count { !it.fromMe && !it.read }
                )
            }.sortedByDescending { it.lastTime }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun conversationFlow(contact: String): StateFlow<List<ChatMessage>> = conversations
        .map { it[contact] ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun sendMessage(contact: String, content: String) {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) return
        val newMessage = ChatMessage(
            author = userAuthor,
            content = trimmed,
            time = LocalTime.now().format(formatter),
            fromMe = true,
            read = true
        )
        appendMessage(contact, newMessage)
        autoReply(contact)
    }

    fun markConversationRead(contact: String) {
        _conversations.value = _conversations.value.toMutableMap().apply {
            val updated = this[contact]?.map { if (it.fromMe) it else it.copy(read = true) }
            if (updated != null) put(contact, updated)
        }
    }

    private fun appendMessage(contact: String, message: ChatMessage) {
        _conversations.value = _conversations.value.toMutableMap().apply {
            val current = this[contact] ?: emptyList()
            put(contact, current + message)
        }
    }

    private fun autoReply(contact: String) {
        viewModelScope.launch {
            delay(1_200)
            val response = autoReplies[contact] ?: "¡Nos vemos en San Telmo!"
            appendMessage(
                contact,
                ChatMessage(
                    author = contact,
                    content = response,
                    time = LocalTime.now().format(formatter),
                    fromMe = false,
                    read = false
                )
            )
        }
    }

    companion object {
        private val autoReplies = mapOf(
            "Tigre Sunset 2025" to "Preparen las cámaras, el muelle estará increíble.",
            "Palermo Sabores Urbanos" to "Mesa reservada, lleguen con apetito.",
            "Recoleta Jazz Nocturno" to "Traigan sus instrumentos, tocamos a las 21 hs."
        )

        private fun initialConversations(): Map<String, List<ChatMessage>> {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val now = LocalTime.now()
            return mapOf(
                "Tigre Sunset 2025" to listOf(
                    ChatMessage(
                        "Tigre Sunset 2025",
                        "¡Hola equipo! ¿Confirmamos la puesta de sol en el delta el sábado?",
                        now.minusMinutes(30).format(formatter),
                        false,
                        false
                    ),
                    ChatMessage(
                        "Tú",
                        "¡Me encanta la idea! ¿Salimos a las 16:00?",
                        now.minusMinutes(25).format(formatter),
                        true,
                        true
                    )
                ),
                "Palermo Sabores Urbanos" to listOf(
                    ChatMessage(
                        "Palermo Sabores Urbanos",
                        "Recorrida gastronómica esta noche, ¿quién trae postre?",
                        now.minusMinutes(10).format(formatter),
                        false,
                        false
                    )
                ),
                "Recoleta Jazz Nocturno" to listOf(
                    ChatMessage(
                        "Recoleta Jazz Nocturno",
                        "Ensayo en la terraza a las 21 hs, ¿se suman?",
                        now.minusMinutes(5).format(formatter),
                        false,
                        false
                    )
                )
            )
        }
    }
}

data class ChatMessage(
    val author: String,
    val content: String,
    val time: String,
    val fromMe: Boolean,
    val read: Boolean
)

data class ChatPreview(
    val contact: String,
    val lastMessage: String,
    val lastTime: String,
    val unreadCount: Int
)

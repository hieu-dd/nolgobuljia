package com.noljanolja.core.utils

import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.Message
import kotlinx.coroutines.flow.*

object Database {
    private var CACHED_TOKEN: String? = null
    private val CACHED_CONVERSATIONS = MutableStateFlow<List<Conversation>>(listOf())

    suspend fun getLocalConversation(conversationId: Long): Flow<Conversation> {
        return CACHED_CONVERSATIONS.mapNotNull {
            it.find { conversation -> conversation.id == conversationId }
        }
    }

    suspend fun getLocalConversations(): Flow<List<Conversation>> {
        return CACHED_CONVERSATIONS
    }

    suspend fun findSingleConversationWithUser(
        userId: String,
    ): Conversation? {
        return with(CACHED_CONVERSATIONS.value) {
            find { it.participants.any { !it.isMe && it.id == userId } }
                ?: find { it.participants.all { it.id == userId } }
        }
    }

    suspend fun upsertConversationMessages(
        conversationId: Long,
        messages: List<Message>,
    ) {
        val value = CACHED_CONVERSATIONS.value
        value.find { it.id == conversationId }?.let {
            updateLocalConversation(
                it.copy(
                    messages = messages
                )
            )
        }
    }

    suspend fun updateLocalConversation(conversation: Conversation) {
        val value = CACHED_CONVERSATIONS.value
        var added = false
        val conversations = mutableListOf<Conversation>().apply {
            value.forEach {
                if (it.id == conversation.id) {
                    added = true
                    add(it.combineConversation(conversation))
                } else {
                    add(it)
                }
            }
        }
        if (!added) {
            conversations.add(conversation)
        }

        CACHED_CONVERSATIONS.emit(
            conversations
        )
    }

    fun getToken() = CACHED_TOKEN
    fun saveToken(token: String) {
        CACHED_TOKEN = token
    }

    suspend fun clear() {
        CACHED_TOKEN = null
        CACHED_CONVERSATIONS.emit(emptyList())
    }
}

private fun Conversation.combineConversation(conversation: Conversation): Conversation {
    return Conversation(
        id = id,
        title = title.takeIf { it.isNotBlank() } ?: conversation.title,
        type = type,
        creator = creator,
        participants = (participants + conversation.participants).distinctBy { it.id },
        messages = (messages + conversation.messages)
            .distinctBy { it.id }
            .sortedByDescending { it.createdAt },
        createdAt = createdAt,
        updatedAt = maxOf(updatedAt, conversation.updatedAt)
    )
}
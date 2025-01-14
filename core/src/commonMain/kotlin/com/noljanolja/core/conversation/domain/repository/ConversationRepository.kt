package com.noljanolja.core.conversation.domain.repository

import com.noljanolja.core.conversation.domain.model.Conversation
import com.noljanolja.core.conversation.domain.model.ConversationMedia
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.model.ReactIcon
import com.noljanolja.core.video.data.model.request.VideoProgressEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

internal interface ConversationRepository {
    val removedConversationEvent: SharedFlow<Conversation>
    suspend fun findConversationWithUsers(userIds: List<String>): Conversation?

    suspend fun getConversation(
        conversationId: Long,
    ): Flow<Conversation>

    suspend fun fetchConversations(): Flow<List<Conversation>>

    suspend fun forceRefreshConversations()

    suspend fun getLocalConversations(): Flow<List<Conversation>>

    suspend fun sendConversationMessage(
        title: String = "",
        conversationId: Long,
        userIds: List<String>,
        message: Message,
        replyToMessageId: Long?,
        shareMessageId: Long?,
        shareVideoId: String?,
    ): Long

    suspend fun sendConversationsMessage(
        conversationIds: List<Long>,
        userIds: List<String>,
        message: Message,
        replyToMessageId: Long?,
        shareMessageId: Long?,
        shareVideoId: String?,
        title: String?,
    ): Result<Boolean>

    suspend fun createConversation(title: String, userIds: List<String>): Long

    suspend fun getConversationMessages(
        conversationId: Long,
        messageBefore: Long? = null,
        messageAfter: Long? = null,
    ): List<Message>

    suspend fun getMessageById(messageId: Long): Message?

    suspend fun updateMessageStatus(
        conversationId: Long,
        messageId: Long,
    )

    suspend fun upsertConversationMessages(
        conversationId: Long,
        messages: List<Message>,
    )

    suspend fun streamConversations(
        token: String? = null,
    )

    suspend fun trackVideoProgress(
        token: String? = null,
        videoId: String,
        event: VideoProgressEvent,
        durationMs: Long,
    )

    suspend fun leaveConversation(conversationId: Long): Result<Boolean>

    suspend fun addParticipants(conversationId: Long, userIds: List<String>): Result<Boolean>

    suspend fun removeParticipants(conversationId: Long, userIds: List<String>): Result<Boolean>

    suspend fun makeConversationAdmin(conversationId: Long, userId: String): Result<Boolean>

    suspend fun updateConversation(conversationId: Long, title: String): Result<Conversation>

    fun getReactIcons(): Flow<List<ReactIcon>>

    suspend fun reactMessage(conversationId: Long, messageId: Long, reactId: Long): Result<Boolean>

    suspend fun deleteMessage(
        conversationId: Long,
        messageId: Long,
        removeForSelfOnly: Boolean,
    ): Result<Boolean>

    suspend fun getConversationAttachments(
        conversationId: Long,
        attachmentTypes: List<String>,
        page: Int,
    ): Result<List<ConversationMedia>>

    fun onDestroy()
}
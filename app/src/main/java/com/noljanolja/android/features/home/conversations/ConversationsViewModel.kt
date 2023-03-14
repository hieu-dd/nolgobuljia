package com.noljanolja.android.features.home.conversations

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.conversation.domain.model.Conversation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect

class ConversationsViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(UiState<List<Conversation>>())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        fetchConversations()
    }

    fun handleEvent(event: ConversationsEvent) {
        launch {
            when (event) {
                is ConversationsEvent.OpenConversation -> {
                    navigationManager.navigate(
                        NavigationDirections.Chat(
                            conversationId = event.conversationId,
                            userId = event.userId,
                            userName = event.userName
                        )
                    )
                }
                is ConversationsEvent.OpenContactPicker -> {
                    navigationManager.navigate(NavigationDirections.SelectContact)
                }
            }
        }
    }

    private fun fetchConversations() {
        launch {
            val value = _uiStateFlow.value
            _uiStateFlow.emit(value.copy(loading = true))
            coreManager.getConversations().collect {
                _uiStateFlow.emit(UiState(data = it))
            }
        }
    }
}
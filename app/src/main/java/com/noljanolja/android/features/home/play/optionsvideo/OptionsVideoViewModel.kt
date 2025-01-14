package com.noljanolja.android.features.home.play.optionsvideo

import androidx.lifecycle.*
import com.noljanolja.android.common.base.BaseShareContactViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.data.*
import com.noljanolja.android.common.error.exceptionOrUnDefined
import com.noljanolja.android.common.navigation.*
import com.noljanolja.android.features.common.ShareContact
import com.noljanolja.android.util.Constant.*
import com.noljanolja.core.conversation.domain.model.Message
import com.noljanolja.core.conversation.domain.model.MessageType
import com.noljanolja.core.video.domain.model.Video
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class OptionsVideoViewModel : BaseShareContactViewModel() {
    private val _showConfirmDialog = MutableStateFlow<ShareToAppData?>(null)
    internal val showConfirmDialog = _showConfirmDialog.asStateFlow()

    protected val _shareSuccessEvent = MutableSharedFlow<String?>()
    val shareSuccessEvent = _shareSuccessEvent.asSharedFlow()

    internal fun copySuccess(message: String) {
        viewModelScope.launch {
            _shareSuccessEvent.emit(message)
        }
    }

    fun changeDialogState(appName: String? = null) {
        val data = when (appName) {
            AppNameShareToApp.FACEBOOK -> ShareToAppData(
                appName = AppNameShareToApp.FACEBOOK,
                packageName = PackageShareToApp.FACEBOOK_PACKAGE
            )

            AppNameShareToApp.TWITTER -> ShareToAppData(
                appName = AppNameShareToApp.TWITTER,
                packageName = PackageShareToApp.TWITTER_PACKAGE
            )

            AppNameShareToApp.WHATS_APP -> ShareToAppData(
                appName = AppNameShareToApp.WHATS_APP,
                packageName = PackageShareToApp.WHATS_APP_PACKAGE
            )

            AppNameShareToApp.TELEGRAM -> ShareToAppData(
                appName = AppNameShareToApp.TELEGRAM,
                packageName = PackageShareToApp.TELEGRAM_PACKAGE
            )

            AppNameShareToApp.MESSENGER -> ShareToAppData(
                appName = AppNameShareToApp.MESSENGER,
                packageName = PackageShareToApp.MESSENGER_PACKAGE
            )

            else -> null
        }
        _showConfirmDialog.value = data
    }

    fun handleEvent(event: OptionsVideoEvent?) {
        launch {
            when (event) {
                is OptionsVideoEvent.ShareVideo -> shareVideo(
                    video = event.video,
                    shareContact = event.shareContact
                )
                else -> {}
            }
        }
    }

    private suspend fun shareVideo(video: Video, shareContact: ShareContact?) {
        val sendMessage = Message(
            message = video.title,
            type = MessageType.PLAINTEXT
        )
        shareContact?.conversationId?.let {
            val result = coreManager.sendConversationsMessage(
                conversationIds = listOf(shareContact.conversationId),
                message = sendMessage,
                userIds = emptyList(),
                shareVideoId = video.id
            )
            if (result.isSuccess) {
                _shareSuccessEvent.emit(null)
                navigationManager.navigate(NavigationDirections.Chat(it))
            } else {
                sendError(result.exceptionOrUnDefined())
            }
            return
        }
        shareContact?.userId?.let {
            val me = coreManager.getCurrentUser().getOrNull()
            val result = coreManager.sendConversationsMessage(
                conversationIds = listOf(0L),
                userIds = listOfNotNull(it, me?.id),
                message = sendMessage,
                shareVideoId = video.id,
                title = shareContact.title
            )
            if (result.isSuccess) {
                _shareSuccessEvent.emit(null)
            } else {
                sendError(result.exceptionOrUnDefined())
            }
            return
        }
    }
}
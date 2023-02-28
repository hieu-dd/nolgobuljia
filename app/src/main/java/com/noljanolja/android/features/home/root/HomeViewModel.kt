package com.noljanolja.android.features.home.root

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.common.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val userRepository: UserRepository,
) : BaseViewModel() {
    private val _showRequireLoginPopupEvent = MutableSharedFlow<Boolean>()
    val showRequireLoginPopupEvent = _showRequireLoginPopupEvent.asSharedFlow()

    fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ChangeNavigationItem -> changeNavigationItem(event.item, event.onChange)
            HomeEvent.LoginOrVerifyEmail -> loginOrVerifyEmail()
        }
    }

    private fun changeNavigationItem(
        item: HomeNavigationItem,
        onChange: () -> Unit,
    ) {
        launch {
            onChange.invoke()
//            if (item == HomeNavigationItem.CelebrationItem) {
//                onChange.invoke()
//                return@launch
//            }
//            val user = userRepository.getCurrentUser()
//            if (user?.isVerify == true) {
//                onChange.invoke()
//            } else {
//                _showRequireLoginPopupEvent.emit(true)
//            }
        }
    }

    private fun loginOrVerifyEmail() {
        launch {
            val user = userRepository.getCurrentUser().getOrNull()
            when {
                // TODO : Check verify if need after
                true -> {
                    _showRequireLoginPopupEvent.emit(false)
                    navigationManager.navigate(NavigationDirections.Auth)
                }
//                !user!!.isVerify -> sendError(Throwable("Verify fail"))
                else -> _showRequireLoginPopupEvent.emit(false)
            }
        }
    }
}
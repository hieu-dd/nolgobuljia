package com.noljanolja.android.features.auth.updateprofile

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.common.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val userRepository: UserRepository,
) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(UpdateProfileUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun handleEvent(event: UpdateProfileEvent) {
        launch {
            when (event) {
                UpdateProfileEvent.DismissError -> {
                    _uiStateFlow.emit(UpdateProfileUiState())
                }
                is UpdateProfileEvent.Update -> update(event.name)
                is UpdateProfileEvent.UploadAvatar -> {
                }
            }
        }
    }

    private suspend fun update(name: String) {
        _uiStateFlow.emit(UpdateProfileUiState(loading = true))
        val result = userRepository.updateUser(name = name, photo = null)
        if (result.isSuccess) {
            _uiStateFlow.emit(UpdateProfileUiState())
            navigationManager.navigate(NavigationDirections.Home)
        } else {
            _uiStateFlow.emit(UpdateProfileUiState(error = result.exceptionOrNull()))
        }
    }
}

data class UpdateProfileUiState(
    val loading: Boolean = false,
    val error: Throwable? = null,
)
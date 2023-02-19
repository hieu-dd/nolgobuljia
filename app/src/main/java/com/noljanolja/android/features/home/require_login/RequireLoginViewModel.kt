package com.noljanolja.android.features.home.require_login

import androidx.lifecycle.viewModelScope
import com.d2brothers.firebase_auth.AuthSdk
import com.noljanolja.android.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RequireLoginViewModel @Inject constructor(
    private val authSdk: AuthSdk,
) : BaseViewModel() {
    val hasUser: StateFlow<Boolean> = authSdk.getCurrentUser().map {
        it != null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )
}

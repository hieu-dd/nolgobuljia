package com.noljanolja.android.features.shop.giftdetail

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.base.launchInMain
import com.noljanolja.android.common.error.UnexpectedFailure
import com.noljanolja.android.common.navigation.*
import com.noljanolja.core.exchange.domain.domain.ExchangeBalance
import com.noljanolja.core.shop.domain.model.Gift
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GiftDetailViewModel(
    private val giftId: String,
    private val code: String,
) : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<GiftDetailUiData>>(
        UiState(
            loading = true
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _myBalanceFlow = MutableStateFlow(ExchangeBalance())

    val myBalanceFlow = _myBalanceFlow.asStateFlow()

    private val _buyGiftSuccessEvent = MutableSharedFlow<Boolean>()
    val buyGiftSuccessEvent = _buyGiftSuccessEvent

    init {
        launch {
            val gift = coreManager.getGiftDetail(giftId).getOrDefault(Gift())
            val giftsByCategory =
                coreManager.getGifts(categoryId = gift.category.id).getOrDefault(emptyList())
                    .toMutableList()
            giftsByCategory.remove(gift)

            _uiStateFlow.emit(
                UiState(
                    data = GiftDetailUiData(
                        gift = gift.copy(qrCode = code),
                        giftsByCategory = giftsByCategory
                    )
                )
            )
        }
        launch {
            coreManager.getExchangeBalance().getOrNull()?.let {
                _myBalanceFlow.emit(it)
            }
        }
    }

    fun handleEvent(event: GiftDetailEvent) {
        launch {
            when (event) {
                GiftDetailEvent.Back -> back()

                GiftDetailEvent.Purchase -> purchase()

                is GiftDetailEvent.GiftDetail -> navigationManager.navigate(
                    NavigationDirections.GiftDetail(event.giftId, event.code)
                )
            }
        }
    }

    private suspend fun purchase() {
        val currentValue = _uiStateFlow.value
        _uiStateFlow.emit(
            UiState(loading = true, data = currentValue.data)
        )
        val response = coreManager.buyGift(giftId)
        response.getOrNull()?.let {
            val giftsByCategory =
                coreManager.getGifts(categoryId = it.category.id).getOrDefault(emptyList())
                    .toMutableList()
            giftsByCategory.remove(it)
            _buyGiftSuccessEvent.emit(true)
            _uiStateFlow.emit(
                UiState(
                    data = GiftDetailUiData(
                        gift = it,
                        giftsByCategory = giftsByCategory
                    )
                )
            )
            launchInMain {
                coreManager.refreshMemberInfo()
            }
        } ?: let {
            sendError(response.exceptionOrNull() ?: UnexpectedFailure)
            _uiStateFlow.emit(
                UiState(loading = false, data = currentValue.data)
            )
        }
    }
}

data class GiftDetailUiData(
    val gift: Gift,
    val giftsByCategory: MutableList<Gift>
)
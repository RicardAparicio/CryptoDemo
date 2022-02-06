package com.ricardaparicio.cryptodemo.features.detail.ui.reducer

import com.ricardaparicio.cryptodemo.core.Reducer
import com.ricardaparicio.cryptodemo.core.UiAction
import com.ricardaparicio.cryptodemo.features.common.domain.model.Coin
import com.ricardaparicio.cryptodemo.features.detail.ui.CoinDetailUiState
import com.ricardaparicio.cryptodemo.features.common.ui.model.model.CoinSummaryUiModel
import javax.inject.Inject

sealed class CoinDetailUiAction : UiAction {
    data class NewCoin(val coin: Coin) : CoinDetailUiAction()
}

class CoinDetailReducer @Inject constructor() : Reducer<CoinDetailUiState, CoinDetailUiAction> {

    override val reduce: (CoinDetailUiState, CoinDetailUiAction) -> CoinDetailUiState =
        { state, action ->
            when (action) {
                is CoinDetailUiAction.NewCoin -> state.copy(
                    coinSummary = CoinSummaryUiModel.from(action.coin.coinSummary),
                    description = action.coin.description,
                )
            }
        }
}
package com.ricardaparicio.cryptodemo.features.detail.ui.reducer

import com.ricardaparicio.cryptodemo.core.Reducer
import com.ricardaparicio.cryptodemo.core.UiAction
import com.ricardaparicio.cryptodemo.core.util.formatPercentage
import com.ricardaparicio.cryptodemo.core.util.formatPrice
import com.ricardaparicio.cryptodemo.features.common.domain.model.Coin
import com.ricardaparicio.cryptodemo.features.common.ui.model.CoinSummaryUiModel
import com.ricardaparicio.cryptodemo.features.common.ui.reducer.ContentLoadingUiAction
import com.ricardaparicio.cryptodemo.features.common.ui.reducer.contentLoadingReduce
import com.ricardaparicio.cryptodemo.features.detail.ui.CoinDetailUiState
import javax.inject.Inject

sealed class CoinDetailUiAction : UiAction {
    data class NewCoin(val coin: Coin) : CoinDetailUiAction()
    data class UpdateContentLoading(val action: ContentLoadingUiAction): CoinDetailUiAction()
}

class CoinDetailReducer @Inject constructor() : Reducer<CoinDetailUiState, CoinDetailUiAction> {

    override val reduce: (CoinDetailUiState, CoinDetailUiAction) -> CoinDetailUiState =
        { state, action ->
            when (action) {
                is CoinDetailUiAction.NewCoin -> {
                    val coin = action.coin
                    state.copy(
                        coinSummary = CoinSummaryUiModel.from(coin.coinSummary),
                        description = coin.description,
                        ath = coin.ath.formatPrice(coin.coinSummary.fiatCurrency),
                        marketCap = coin.marketCap.formatPrice(coin.coinSummary.fiatCurrency),
                        priceChange24h = coin.priceChange24h.formatPercentage(),
                        priceChangePercentage24h = coin.priceChangePercentage24h.formatPercentage(),
                    )
                }
                is CoinDetailUiAction.UpdateContentLoading -> {
                    state.copy(
                        contentLoadingUiState = contentLoadingReduce(state.contentLoadingUiState, action.action)
                    )
                }
            }
        }
}
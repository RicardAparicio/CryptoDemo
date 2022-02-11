package com.ricardaparicio.cryptodemo.features.list.ui.reducer

import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.core.Reducer
import com.ricardaparicio.cryptodemo.core.UiAction
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinSummary
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import com.ricardaparicio.cryptodemo.features.common.ui.model.AlertErrorUiModel
import com.ricardaparicio.cryptodemo.features.common.ui.model.CoinSummaryUiModel
import com.ricardaparicio.cryptodemo.features.list.ui.CoinListUiState
import javax.inject.Inject

sealed class CoinListUiAction : UiAction {
    object Loading : CoinListUiAction()
    data class NewCoins(val coins: List<CoinSummary>) : CoinListUiAction()
    data class UpdateFiatCurrency(val currency: FiatCurrency) : CoinListUiAction()
    data class ErrorUpdateFiatCurrency(val currency: FiatCurrency) : CoinListUiAction()
    data class Error(val failure: Failure) : CoinListUiAction()
    object DismissError : CoinListUiAction()
}

class CoinListReducer @Inject constructor() : Reducer<CoinListUiState, CoinListUiAction> {

    override val reduce: (CoinListUiState, CoinListUiAction) -> CoinListUiState = { state, action ->
        when (action) {
            is CoinListUiAction.NewCoins -> {
                state.copy(
                    coins = action.coins.map { coinSummary ->
                        CoinSummaryUiModel.from(coinSummary)
                    },
                    loading = false,
                )
            }
            is CoinListUiAction.ErrorUpdateFiatCurrency -> {
                state.copy(
                    fiatCurrency = action.currency,
                    loading = false,
                )
            }
            is CoinListUiAction.UpdateFiatCurrency -> {
                state.copy(
                    fiatCurrency = when (action.currency) {
                        FiatCurrency.Eur -> FiatCurrency.Usd
                        FiatCurrency.Usd -> FiatCurrency.Eur
                    },
                )
            }
            CoinListUiAction.Loading -> {
                state.copy(
                    loading = true,
                )
            }
            CoinListUiAction.DismissError -> {
                state.copy(
                    error = null
                )
            }
            is CoinListUiAction.Error -> {
                state.copy(
                    error = AlertErrorUiModel.from(action.failure),
                    loading = false,
                )
            }
        }
    }
}
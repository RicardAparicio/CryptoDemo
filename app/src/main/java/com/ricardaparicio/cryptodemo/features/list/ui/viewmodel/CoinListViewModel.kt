package com.ricardaparicio.cryptodemo.features.list.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.core.Reducer
import com.ricardaparicio.cryptodemo.core.usecase.NoParam
import com.ricardaparicio.cryptodemo.core.util.doNothing
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import com.ricardaparicio.cryptodemo.features.list.domain.GetCoinListUseCase
import com.ricardaparicio.cryptodemo.features.list.domain.UpdateFiatCurrencyUseCase
import com.ricardaparicio.cryptodemo.features.list.ui.CoinListUiState
import com.ricardaparicio.cryptodemo.features.list.ui.reducer.CoinListUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel
@Inject constructor(
    private val getCoinListUseCase: GetCoinListUseCase,
    private val updateFiatCurrencyUseCase: UpdateFiatCurrencyUseCase,
    private val reducer: Reducer<CoinListUiState, CoinListUiAction>,
) : ViewModel() {

    var uiState by mutableStateOf(CoinListUiState())
        private set

    init {
        fetchCoins()
    }

    fun onFiatCurrencyClicked(currency: FiatCurrency) {
        reduce(CoinListUiAction.UpdateFiatCurrency(currency))
        updateFiatCurrency(currency)
    }

    private fun updateFiatCurrency(currency: FiatCurrency) {
        viewModelScope.launch {
            // Progress
            updateFiatCurrencyUseCase(UpdateFiatCurrencyUseCase.Params(currency))
                .fold(
                    {
                        reduce(CoinListUiAction.ErrorFiatCurrencyUpdate(currency))
                    },
                    {
                        // finish progress
                    }
                )
        }
    }

    private fun fetchCoins() =
        getCoinListUseCase(NoParam)
            .onEach { result -> foldCoinListResult(result) }
            .launchIn(viewModelScope)

    private fun foldCoinListResult(result: Either<Failure, GetCoinListUseCase.Result>) =
        result.fold(
            {
                doNothing()
            },
            { useCaseResult ->
                reduce(CoinListUiAction.NewCoins(useCaseResult.coins))
            }
        )

    private fun reduce(action: CoinListUiAction) {
        uiState = reducer.reduce(uiState, action)
    }
}
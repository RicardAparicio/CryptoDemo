package com.ricardaparicio.cryptodemo.features.list.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import arrow.core.Either
import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.core.Reducer
import com.ricardaparicio.cryptodemo.core.usecase.NoParam
import com.ricardaparicio.cryptodemo.core.util.doNothing
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinListState
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import com.ricardaparicio.cryptodemo.features.list.domain.GetCoinListUseCase
import com.ricardaparicio.cryptodemo.features.list.domain.GetFiatCurrencyUseCase
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
    private val getFiatCurrencyUseCase: GetFiatCurrencyUseCase,
    private val updateFiatCurrencyUseCase: UpdateFiatCurrencyUseCase,
    private val reducer: Reducer<CoinListUiState, CoinListUiAction>,
) : ViewModel() {

    var uiState by mutableStateOf(CoinListUiState())
        private set

    init {
        viewModelScope.launch {
            fetchFiatCurrency()
            fetchCoins()
        }
    }

    private suspend fun fetchFiatCurrency() {
        getFiatCurrencyUseCase(NoParam)
            .fold(
                { failure ->
                    reduce(CoinListUiAction.Error(failure))
                },
                { result ->
                    reduce(CoinListUiAction.UpdateFiatCurrency(result.currency))
                }
            )
    }

    fun onFiatCurrencyClicked(currency: FiatCurrency) {
        updateFiatCurrency(currency)
    }

    private fun updateFiatCurrency(currency: FiatCurrency) {
        viewModelScope.launch {
            updateFiatCurrencyUseCase(UpdateFiatCurrencyUseCase.Params(currency))
                .fold(
                    { failure ->
                        reduce(CoinListUiAction.Error(failure))
                    },
                    { result ->
                        reduce(CoinListUiAction.UpdateFiatCurrency(result.currency))

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
            { failure ->
                reduce(CoinListUiAction.Error(failure))
            },
            { useCaseResult ->
                reduceCoinListResult(useCaseResult)
            }
        )

    private fun reduceCoinListResult(useCaseResult: GetCoinListUseCase.Result) =
        when (val coinState = useCaseResult.coinState) {
            is CoinListState.Coins -> {
                reduce(CoinListUiAction.NewCoins(coinState.coins))
            }
            CoinListState.Loading -> {
                reduce(CoinListUiAction.Loading)
            }
        }

    private fun reduce(action: CoinListUiAction) {
        uiState = reducer.reduce(uiState, action)
    }

    fun onDismissDialogRequested() {
        reduce(CoinListUiAction.DismissError)
    }
}
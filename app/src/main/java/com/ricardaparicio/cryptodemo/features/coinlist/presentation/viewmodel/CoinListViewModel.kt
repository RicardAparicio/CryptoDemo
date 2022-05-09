/*
 * Copyright 2022 Ricard Aparicio

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ricardaparicio.cryptodemo.features.coinlist.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.core.Reducer
import com.ricardaparicio.cryptodemo.core.usecase.NoParam
import com.ricardaparicio.cryptodemo.features.coinlist.domain.GetCoinListUseCase
import com.ricardaparicio.cryptodemo.features.coinlist.domain.GetFiatCurrencyUseCase
import com.ricardaparicio.cryptodemo.features.coinlist.domain.UpdateFiatCurrencyUseCase
import com.ricardaparicio.cryptodemo.features.coinlist.presentation.reducer.CoinListUiAction
import com.ricardaparicio.cryptodemo.features.coinlist.presentation.reducer.CoinListUiAction.*
import com.ricardaparicio.cryptodemo.features.coinlist.presentation.ui.CoinListUiState
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinListState
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import com.ricardaparicio.cryptodemo.features.common.ui.viewmodel.ContentLoadingUiAction
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
            getFiatCurrency()
            fetchCoins()
        }
    }

    private suspend fun getFiatCurrency() =
        getFiatCurrencyUseCase(NoParam)
            .fold(
                { failure ->
                    reduce(UpdateContentLoading(ContentLoadingUiAction.Error(failure)))
                },
                { result ->
                    reduce(UpdateFiatCurrency(result.currency))
                }
            )

    private fun updateFiatCurrency(currency: FiatCurrency) =
        viewModelScope.launch {
            updateFiatCurrencyUseCase(UpdateFiatCurrencyUseCase.Params(currency))
                .fold(
                    { failure ->
                        reduce(UpdateContentLoading(ContentLoadingUiAction.Error(failure)))
                    },
                    { result ->
                        reduce(UpdateFiatCurrency(result.currency))
                    }
                )
        }

    private fun fetchCoins() =
        getCoinListUseCase(NoParam)
            .onEach { result -> foldCoinListResult(result) }
            .launchIn(viewModelScope)

    private fun foldCoinListResult(result: Either<Failure, GetCoinListUseCase.Result>) =
        result.fold(
            { failure ->
                reduce(UpdateContentLoading(ContentLoadingUiAction.Error(failure)))
            },
            { useCaseResult ->
                reduceCoinListResult(useCaseResult)
            }
        )

    private fun reduceCoinListResult(useCaseResult: GetCoinListUseCase.Result) =
        when (val coinState = useCaseResult.coinState) {
            is CoinListState.Coins -> {
                reduce(NewCoins(coinState.coins))
                reduce(UpdateContentLoading(ContentLoadingUiAction.Success))
            }
            CoinListState.Loading -> {
                reduce(UpdateContentLoading(ContentLoadingUiAction.Loading))
            }
        }

    private fun reduce(action: CoinListUiAction) {
        uiState = reducer.reduce(uiState, action)
    }

    fun onFiatCurrencyClicked(currency: FiatCurrency) {
        updateFiatCurrency(currency)
    }

    fun onDismissDialogRequested() {
        reduce(UpdateContentLoading(ContentLoadingUiAction.CloseError))
    }
}
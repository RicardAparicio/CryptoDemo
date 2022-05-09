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
package com.ricardaparicio.cryptodemo.features.coindetail.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricardaparicio.cryptodemo.core.Reducer
import com.ricardaparicio.cryptodemo.core.navigation.NavArg
import com.ricardaparicio.cryptodemo.features.common.ui.viewmodel.ContentLoadingUiAction
import com.ricardaparicio.cryptodemo.features.coindetail.domain.GetCoinUseCase
import com.ricardaparicio.cryptodemo.features.coindetail.presentation.ui.CoinDetailUiState
import com.ricardaparicio.cryptodemo.features.coindetail.presentation.reducer.CoinDetailUiAction
import com.ricardaparicio.cryptodemo.features.coindetail.presentation.reducer.CoinDetailUiAction.NewCoin
import com.ricardaparicio.cryptodemo.features.coindetail.presentation.reducer.CoinDetailUiAction.UpdateContentLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val getCoinUseCase: GetCoinUseCase,
    private val reducer: Reducer<CoinDetailUiState, CoinDetailUiAction>,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var uiState by mutableStateOf(CoinDetailUiState())
        private set

    private val coinId: String
        get() = requireNotNull(
            savedStateHandle.get<String>(NavArg.CoinId.key)
        )

    init {
        fetchCoin()
    }

    private fun fetchCoin() =
        viewModelScope.launch {
            reduce(UpdateContentLoading(ContentLoadingUiAction.Loading))
            getCoinUseCase(GetCoinUseCase.Params(coinId))
                .fold(
                    { failure ->
                        reduce(UpdateContentLoading(ContentLoadingUiAction.Error(failure)))
                    },
                    { result ->
                        reduce(NewCoin(result.coin))
                        reduce(UpdateContentLoading(ContentLoadingUiAction.Success))
                    }
                )
        }

    private fun reduce(action: CoinDetailUiAction) {
        uiState = reducer.reduce(uiState, action)
    }

    fun onDismissDialogRequested() {
        reduce(UpdateContentLoading(ContentLoadingUiAction.CloseError))
    }

}
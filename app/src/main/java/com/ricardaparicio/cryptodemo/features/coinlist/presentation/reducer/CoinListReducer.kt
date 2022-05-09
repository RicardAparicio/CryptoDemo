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
package com.ricardaparicio.cryptodemo.features.coinlist.presentation.reducer

import com.ricardaparicio.cryptodemo.core.Reducer
import com.ricardaparicio.cryptodemo.core.UiAction
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinSummary
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import com.ricardaparicio.cryptodemo.features.common.ui.model.CoinSummaryUiModel
import com.ricardaparicio.cryptodemo.features.common.ui.viewmodel.ContentLoadingReducer
import com.ricardaparicio.cryptodemo.features.common.ui.viewmodel.ContentLoadingUiAction
import com.ricardaparicio.cryptodemo.features.coinlist.presentation.ui.CoinListUiState
import javax.inject.Inject

sealed class CoinListUiAction : UiAction {
    data class NewCoins(val coins: List<CoinSummary>) : CoinListUiAction()
    data class UpdateFiatCurrency(val currency: FiatCurrency) : CoinListUiAction()
    data class UpdateContentLoading(val action: ContentLoadingUiAction) : CoinListUiAction()
}

class CoinListReducer @Inject constructor(
    contentLoadingReducer: ContentLoadingReducer
) : Reducer<CoinListUiState, CoinListUiAction> {

    override val reduce: (CoinListUiState, CoinListUiAction) -> CoinListUiState = { state, action ->
        when (action) {
            is CoinListUiAction.NewCoins -> {
                state.copy(
                    coins = action.coins.map { coinSummary ->
                        CoinSummaryUiModel.from(coinSummary)
                    },
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
            is CoinListUiAction.UpdateContentLoading -> {
                state.copy(
                    contentLoadingUiState = contentLoadingReducer.reduce(
                        state.contentLoadingUiState,
                        action.action
                    )
                )
            }
        }
    }
}
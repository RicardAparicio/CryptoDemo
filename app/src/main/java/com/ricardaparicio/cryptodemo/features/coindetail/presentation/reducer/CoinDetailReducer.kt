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
package com.ricardaparicio.cryptodemo.features.coindetail.presentation.reducer

import com.ricardaparicio.cryptodemo.core.Reducer
import com.ricardaparicio.cryptodemo.core.UiAction
import com.ricardaparicio.cryptodemo.core.util.formatPercentage
import com.ricardaparicio.cryptodemo.core.util.formatPrice
import com.ricardaparicio.cryptodemo.features.common.domain.model.Coin
import com.ricardaparicio.cryptodemo.features.common.ui.model.CoinSummaryUiModel
import com.ricardaparicio.cryptodemo.features.common.ui.viewmodel.ContentLoadingReducer
import com.ricardaparicio.cryptodemo.features.common.ui.viewmodel.ContentLoadingUiAction
import com.ricardaparicio.cryptodemo.features.coindetail.presentation.ui.CoinDetailUiState
import javax.inject.Inject

sealed class CoinDetailUiAction : UiAction {
    data class NewCoin(val coin: Coin) : CoinDetailUiAction()
    data class UpdateContentLoading(val action: ContentLoadingUiAction) : CoinDetailUiAction()
}

class CoinDetailReducer @Inject constructor(
    contentLoadingReducer: ContentLoadingReducer
) : Reducer<CoinDetailUiState, CoinDetailUiAction> {

    override val reduce: (CoinDetailUiState, CoinDetailUiAction) -> CoinDetailUiState =
        { state, action ->
            when (action) {
                is CoinDetailUiAction.NewCoin -> {
                    val coin = action.coin
                    val fiatCurrency = coin.coinSummary.fiatCurrency
                    state.copy(
                        coinSummary = CoinSummaryUiModel.from(coin.coinSummary),
                        description = coin.description,
                        ath = coin.ath.formatPrice(fiatCurrency),
                        marketCap = coin.marketCap.formatPrice(fiatCurrency),
                        priceChange24h = coin.priceChange24h.formatPrice(fiatCurrency),
                        priceChangePercentage24h = coin.priceChangePercentage24h.formatPercentage(),
                    )
                }
                is CoinDetailUiAction.UpdateContentLoading -> {
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
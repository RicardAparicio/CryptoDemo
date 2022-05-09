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

import com.ricardaparicio.cryptodemo.core.util.formatPercentage
import com.ricardaparicio.cryptodemo.core.util.formatPrice
import com.ricardaparicio.cryptodemo.features.FastMock
import com.ricardaparicio.cryptodemo.features.coin
import com.ricardaparicio.cryptodemo.features.coinSummary
import com.ricardaparicio.cryptodemo.features.coindetail.presentation.ui.CoinDetailUiState
import com.ricardaparicio.cryptodemo.features.common.ui.ContentLoadingUiState
import com.ricardaparicio.cryptodemo.features.common.ui.model.CoinSummaryUiModel
import com.ricardaparicio.cryptodemo.features.common.ui.viewmodel.ContentLoadingReducer
import com.ricardaparicio.cryptodemo.features.common.ui.viewmodel.ContentLoadingUiAction
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class CoinDetailReducerTest {
    @MockK
    private lateinit var contentLoadingReducer: ContentLoadingReducer
    private lateinit var reducer: CoinDetailReducer

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        reducer = CoinDetailReducer(contentLoadingReducer)
    }

    @Test
    fun `when NewCoin action is requested then update the state properly`() {
        FastMock.numberFormatExt()
        every { coinSummary.price.formatPrice(any()) } returns "€"
        every { coin.ath.formatPrice(any()) } returns "€"
        every { coin.marketCap.formatPrice(any()) } returns "€"
        every { coin.priceChange24h.formatPrice(any()) } returns "€"
        every { coin.priceChangePercentage24h.formatPercentage() } returns "%"

        val (summary, description, ath, marketCap, priceChange24h, priceChangePercentage24h, contentLoadingUiState) =
            reducer.reduce(
                CoinDetailUiState(),
                CoinDetailUiAction.NewCoin(coin)
            )

        verify(exactly = 1) {
            coinSummary.price.formatPrice(coinSummary.fiatCurrency)
            coin.ath.formatPrice(any())
            coin.marketCap.formatPrice(any())
            coin.priceChange24h.formatPrice(any())
            coin.priceChangePercentage24h.formatPercentage()
        }
        assert(summary == CoinSummaryUiModel.from(coin.coinSummary))
        assert(description == coin.description)
        assert(ath == "€")
        assert(marketCap == "€")
        assert(priceChange24h == "€")
        assert(priceChangePercentage24h == "%")
    }

    @Test
    fun `when UpdateContentLoading action is requested then update the state properly`() {
        every { contentLoadingReducer.reduce(any(), any()) } returns ContentLoadingUiState()

        val state = reducer.reduce(
            CoinDetailUiState(),
            CoinDetailUiAction.UpdateContentLoading(ContentLoadingUiAction.Loading)
        )

        verify(exactly = 1) {
            contentLoadingReducer.reduce(any(), ContentLoadingUiAction.Loading)
        }

        assert(state.contentLoadingUiState == ContentLoadingUiState() )

    }
}
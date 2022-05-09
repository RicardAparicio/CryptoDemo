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
package com.ricardaparicio.cryptodemo.features.common.data.datasource

import androidx.core.text.HtmlCompat
import com.ricardaparicio.cryptodemo.features.common.data.api.model.CoinApiModel
import com.ricardaparicio.cryptodemo.features.common.data.api.model.CoinSummaryApiModel
import com.ricardaparicio.cryptodemo.features.common.domain.model.Coin
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinSummary
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import javax.inject.Inject

class CoinApiMapper @Inject constructor() {

    fun mapCoinSummary(coinSummaryApiModel: CoinSummaryApiModel, currency: FiatCurrency): CoinSummary =
        CoinSummary(
            id = coinSummaryApiModel.id,
            symbol = coinSummaryApiModel.symbol,
            name = coinSummaryApiModel.name,
            image = coinSummaryApiModel.image,
            price = coinSummaryApiModel.current_price,
            marketCapRank = coinSummaryApiModel.market_cap_rank,
            fiatCurrency = currency,
        )

    fun mapCoin(coinApiModel: CoinApiModel, currency: FiatCurrency): Coin =
        Coin(
            coinSummary = CoinSummary(
                id = coinApiModel.id,
                symbol = coinApiModel.symbol,
                name = coinApiModel.name,
                image = coinApiModel.image.large,
                price = when (currency) {
                    FiatCurrency.Eur -> coinApiModel.market_data.current_price.eur
                    FiatCurrency.Usd -> coinApiModel.market_data.current_price.usd
                },
                marketCapRank = coinApiModel.market_cap_rank,
                fiatCurrency = currency
            ),
            description = HtmlCompat.fromHtml(coinApiModel.description.es, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
            ath = when (currency) {
                FiatCurrency.Eur -> coinApiModel.market_data.ath.eur
                FiatCurrency.Usd -> coinApiModel.market_data.ath.usd
            },
            marketCap = when (currency) {
                FiatCurrency.Eur -> coinApiModel.market_data.market_cap.eur
                FiatCurrency.Usd -> coinApiModel.market_data.market_cap.usd
            },
            priceChange24h = coinApiModel.market_data.price_change_24h,
            priceChangePercentage24h = coinApiModel.market_data.price_change_percentage_24h / 100,
        )
}
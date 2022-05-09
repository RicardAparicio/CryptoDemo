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
package com.ricardaparicio.cryptodemo.features

import com.ricardaparicio.cryptodemo.features.common.domain.model.Coin
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinListState
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinSummary
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency

const val COIN_ID = "btc"
val fiatCurrency = FiatCurrency.Eur
val coinsState = CoinListState.Coins(emptyList())
val coinSummary = CoinSummary(
    id = "btc",
    symbol = "BTC",
    name = "bitcoin",
    image = "",
    price = 34.000f,
    marketCapRank = 1,
    fiatCurrency = FiatCurrency.Eur
)
val coin = Coin(
    coinSummary = coinSummary,
    description = "Satoshi Nakamoto",
    ath = 65000f,
    marketCap = 800000000f,
    priceChange24h = 2000f,
    priceChangePercentage24h = 3f,
)
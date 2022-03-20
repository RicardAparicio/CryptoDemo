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
package com.ricardaparicio.cryptodemo.features.common.ui.model.model

import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinSummary
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency

data class CoinSummaryUiModel(
    val id: String = "",
    val marketCapPosition: String = "",
    val symbol: String = "",
    val name: String = "",
    val image: String = "",
    val price: String = "",
) {
    companion object {
        fun from(coinSummary: CoinSummary): CoinSummaryUiModel =
            CoinSummaryUiModel(
                id = coinSummary.id,
                symbol = coinSummary.symbol,
                name = coinSummary.name,
                image = coinSummary.image,
                price =
                """ 
                    ${coinSummary.price}
                    ${
                       when (coinSummary.fiatCurrency) {
                           FiatCurrency.Eur -> "€"
                           FiatCurrency.Usd -> "$"
                       }
                    }
                """,
                marketCapPosition = coinSummary.marketCapRank.toString()
            )
    }
}
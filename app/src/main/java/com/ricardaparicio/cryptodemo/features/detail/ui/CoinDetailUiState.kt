package com.ricardaparicio.cryptodemo.features.detail.ui

import com.ricardaparicio.cryptodemo.features.common.ui.model.CoinSummaryUiModel


data class CoinDetailUiState(
    val coinSummary: CoinSummaryUiModel = CoinSummaryUiModel(),
    val description: String = "",
    val ath: String = "",
    val marketCap: String = "",
    val priceChange24h: String = "",
    val priceChangePercentage24h: String = "",
)

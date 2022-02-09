package com.ricardaparicio.cryptodemo.features.detail.ui

import com.ricardaparicio.cryptodemo.features.common.ui.model.model.CoinSummaryUiModel

data class CoinDetailUiState(
    val coinSummary: CoinSummaryUiModel = CoinSummaryUiModel(),
    val description: String = "",
)
package com.ricardaparicio.cryptodemo.features.list.ui

import com.ricardaparicio.cryptodemo.features.list.ui.model.CoinSummaryUiModel

data class CoinListUiState(
    val coins: List<CoinSummaryUiModel> = emptyList(),
)

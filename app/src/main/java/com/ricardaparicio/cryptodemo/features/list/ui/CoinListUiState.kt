package com.ricardaparicio.cryptodemo.features.list.ui

import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import com.ricardaparicio.cryptodemo.features.common.ui.model.model.CoinSummaryUiModel

data class CoinListUiState(
    val coins: List<CoinSummaryUiModel> = emptyList(),
    val fiatCurrency: FiatCurrency = FiatCurrency.Eur,
    val loading: Boolean = false,
)

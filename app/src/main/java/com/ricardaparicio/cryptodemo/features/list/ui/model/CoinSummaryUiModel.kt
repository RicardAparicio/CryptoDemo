package com.ricardaparicio.cryptodemo.features.list.ui.model

data class CoinSummaryUiModel(
    val id: String,
    val position: String,
    val symbol: String,
    val name: String,
    val image: String,
    val price: String,
)
package com.ricardaparicio.cryptodemo.features.common.data.model

data class CoinSummaryApiModel(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val current_price: Float,
)
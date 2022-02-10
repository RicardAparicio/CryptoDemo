package com.ricardaparicio.cryptodemo.core.util

import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import java.text.NumberFormat
import java.util.*

fun Float.formatPrice(fiatCurrency: FiatCurrency): String {
    val format: NumberFormat = NumberFormat.getCurrencyInstance()
    format.currency = Currency.getInstance(
        when (fiatCurrency) {
            FiatCurrency.Eur -> "EUR"
            FiatCurrency.Usd -> "USD"
        }
    )
    return format.format(this)
}

fun Float.formatPercentage(): String = NumberFormat.getPercentInstance().format(this)
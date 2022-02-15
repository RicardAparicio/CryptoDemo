package com.ricardaparicio.cryptodemo.core.util

import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

fun Float.formatPrice(fiatCurrency: FiatCurrency): String =
    NumberFormat.getCurrencyInstance().run {
        currency = Currency.getInstance(
            when (fiatCurrency) {
                FiatCurrency.Eur -> "EUR"
                FiatCurrency.Usd -> "USD"
            }
        )
        format(this@formatPrice)
    }

fun Float.formatPercentage(): String =
    NumberFormat.getPercentInstance().run {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
        format(this@formatPercentage)
    }
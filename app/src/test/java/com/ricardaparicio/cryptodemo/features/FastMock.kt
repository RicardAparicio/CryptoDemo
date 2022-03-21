package com.ricardaparicio.cryptodemo.features

import io.mockk.mockkStatic

object FastMock {
    fun numberFormatExt() = mockkStatic("com.ricardaparicio.cryptodemo.core.util.NumberFormatExt")
}
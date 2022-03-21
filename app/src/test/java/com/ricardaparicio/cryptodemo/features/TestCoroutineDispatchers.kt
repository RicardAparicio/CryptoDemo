package com.ricardaparicio.cryptodemo.features

import com.ricardaparicio.cryptodemo.core.CoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@ExperimentalCoroutinesApi
object TestCoroutineDispatchers : CoroutineDispatchers {
    override val io: CoroutineDispatcher
        get() = UnconfinedTestDispatcher()
    override val main: CoroutineDispatcher
        get() = UnconfinedTestDispatcher()
}
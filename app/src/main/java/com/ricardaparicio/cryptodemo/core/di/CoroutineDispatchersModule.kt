package com.ricardaparicio.cryptodemo.core.di

import com.ricardaparicio.cryptodemo.core.CoroutineDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class CoroutineDispatchersModule {
    @Provides
    fun provideCoroutineDispatchers() = object : CoroutineDispatchers {
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
    }
}
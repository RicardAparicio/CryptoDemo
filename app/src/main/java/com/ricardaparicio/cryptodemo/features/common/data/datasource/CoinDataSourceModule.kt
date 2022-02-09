package com.ricardaparicio.cryptodemo.features.common.data.datasource

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CoinDataSourceModule {
    @Binds
    @Singleton
    fun provideCoinRemoteDataSource(coinRetrofitDataSource: CoinRetrofitDataSource): CoinRemoteDataSource

    @Binds
    @Singleton
    fun provideCoinLocalDataSource(coinPreferencesDataSource: CoinPreferencesDataSource): CoinLocalDataSource
}
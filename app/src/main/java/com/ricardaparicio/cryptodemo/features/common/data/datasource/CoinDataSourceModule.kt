package com.ricardaparicio.cryptodemo.features.common.data.datasource

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface CoinDataSourceModule {
    @Binds
    fun provideCoinRemoteDataSource(coinRetrofitDataSource: CoinRetrofitDataSource): CoinRemoteDataSource
}
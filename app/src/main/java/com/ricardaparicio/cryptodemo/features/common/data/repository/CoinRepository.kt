package com.ricardaparicio.cryptodemo.features.common.data.repository

import com.ricardaparicio.cryptodemo.features.common.data.datasource.CoinRemoteDataSource
import javax.inject.Inject

class CoinRepository
@Inject constructor(
    private val coinRemoteDataSource: CoinRemoteDataSource
) {

    suspend fun getCoin(coinId: String) = coinRemoteDataSource.getCoin(coinId)

    suspend fun getCoinList() = coinRemoteDataSource.getCoinList()

}
package com.ricardaparicio.cryptodemo.features.common.data.repository

import arrow.core.Either
import arrow.core.flatMap
import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.features.common.data.datasource.CoinLocalDataSource
import com.ricardaparicio.cryptodemo.features.common.data.datasource.CoinRemoteDataSource
import com.ricardaparicio.cryptodemo.features.common.domain.model.Coin
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinSummary
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CoinRepository
@Inject constructor(
    private val coinRemoteDataSource: CoinRemoteDataSource,
    private val coinLocalDataSource: CoinLocalDataSource,
) {
    suspend fun updateFiatCurrency(currency: FiatCurrency): Either<Failure, Unit> =
        coinLocalDataSource.updateFiatCurrency(currency)

    suspend fun getCoin(coinId: String): Either<Failure, Coin> =
        coinLocalDataSource.fiatCurrencyFlow().first().flatMap { currency ->
            coinRemoteDataSource.getCoin(coinId, currency)
        }

    suspend fun getCoinList(): Flow<Either<Failure, List<CoinSummary>>> =
        coinLocalDataSource.fiatCurrencyFlow().map { currencyResult ->
            currencyResult.flatMap { currency ->
                coinRemoteDataSource.getCoinList(currency)
            }
        }

}
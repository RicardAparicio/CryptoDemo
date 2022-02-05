package com.ricardaparicio.cryptodemo.features.common.data.datasource

import arrow.core.Either
import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.features.common.domain.model.Coin
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinSummary

interface CoinRemoteDataSource {
    suspend fun getCoinList(): Either<Failure, List<CoinSummary>>
    suspend fun getCoin(coinId: String): Either<Failure, Coin>
}
/*
 * Copyright 2022 Ricard Aparicio

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ricardaparicio.cryptodemo.features.common.data.repository

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.features.common.data.datasource.CoinLocalDataSource
import com.ricardaparicio.cryptodemo.features.common.data.datasource.CoinRemoteDataSource
import com.ricardaparicio.cryptodemo.features.common.domain.model.Coin
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinListState
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CoinRepository
@Inject constructor(
    private val coinRemoteDataSource: CoinRemoteDataSource,
    private val coinLocalDataSource: CoinLocalDataSource,
) {

    suspend fun getFiatCurrency(): Either<Failure, FiatCurrency> =
        coinLocalDataSource.fiatCurrencyFlow().first()

    suspend fun updateFiatCurrency(currency: FiatCurrency): Either<Failure, FiatCurrency> =
        coinLocalDataSource.updateFiatCurrency(currency).flatMap {
            coinLocalDataSource.fiatCurrencyFlow().first()
        }

    suspend fun getCoin(coinId: String): Either<Failure, Coin> =
        coinLocalDataSource.fiatCurrencyFlow().first().flatMap { currency ->
            coinRemoteDataSource.getCoin(coinId, currency)
        }

    fun getCoinList(): Flow<Either<Failure, CoinListState>> =
        flow {
            coinLocalDataSource.fiatCurrencyFlow().collect { fiatResult ->
                emit((CoinListState.Loading.right()))
                emit(fiatResult.flatMapToCoins())
            }
        }

    private suspend fun Either<Failure, FiatCurrency>.flatMapToCoins(): Either<Failure, CoinListState.Coins> =
        flatMap { currency ->
            coinRemoteDataSource.getCoinList(currency).map { coins ->
                CoinListState.Coins(coins)
            }
        }
}
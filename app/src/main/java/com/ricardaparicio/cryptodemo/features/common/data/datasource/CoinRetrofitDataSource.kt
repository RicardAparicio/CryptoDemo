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
package com.ricardaparicio.cryptodemo.features.common.data.datasource

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.core.NetworkingError
import com.ricardaparicio.cryptodemo.core.ServerError
import com.ricardaparicio.cryptodemo.features.common.data.api.CoinApiService
import com.ricardaparicio.cryptodemo.features.common.domain.model.Coin
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinSummary
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import retrofit2.Call
import timber.log.Timber
import java.lang.IllegalArgumentException
import javax.inject.Inject

class CoinRetrofitDataSource
@Inject constructor(
    private val coinService: CoinApiService,
    private val coinMapper: CoinApiMapper,
) : CoinRemoteDataSource {

    override suspend fun getCoin(coinId: String, currency: FiatCurrency): Either<Failure, Coin> =
        request(
            call = coinService.getCoin(coinId),
            mapping = { coinApiModel ->
                coinMapper.mapCoin(coinApiModel, currency)
            }
        )

    override suspend fun getCoinList(currency: FiatCurrency): Either<Failure, List<CoinSummary>> =
        request(
            call = coinService.getCoins(
                currency = when (currency) {
                    FiatCurrency.Eur -> EUR_SYMBOL
                    FiatCurrency.Usd -> USD_SYMBOL
                }
            ),
            mapping = { coinsSummaryApiModel ->
                coinsSummaryApiModel.map { coinSummaryApiModel ->
                    coinMapper.mapCoinSummary(coinSummaryApiModel, currency)
                }
            }
        )

    private fun <T, R> request(
        call: Call<T>,
        mapping: (T) -> R,
    ): Either<Failure, R> =
        kotlin.runCatching {
            val response = call.execute()
            when (response.isSuccessful) {
                true -> mapping(requireNotNull(response.body())).right()
                false -> ServerError.left()
            }
        }.onFailure { throwable ->
            Timber.e(throwable)
        }.getOrElse {
            NetworkingError.left()
        }

    companion object {
        private const val EUR_SYMBOL = "eur"
        private const val USD_SYMBOL = "usd"
    }
}
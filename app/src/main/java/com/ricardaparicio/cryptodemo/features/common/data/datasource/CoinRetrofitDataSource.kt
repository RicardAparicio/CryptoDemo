package com.ricardaparicio.cryptodemo.features.common.data.datasource

import arrow.core.Either
import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.core.NetworkingError
import com.ricardaparicio.cryptodemo.core.ServerError
import com.ricardaparicio.cryptodemo.features.common.data.api.CoinApiService
import com.ricardaparicio.cryptodemo.features.common.data.mapper.CoinMapper
import com.ricardaparicio.cryptodemo.features.common.domain.model.Coin
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinSummary
import retrofit2.Call
import timber.log.Timber
import javax.inject.Inject

class CoinRetrofitDataSource
@Inject constructor(
    private val coinService: CoinApiService,
    private val coinMapper: CoinMapper,
) : CoinRemoteDataSource {

    override suspend fun getCoin(coinId: String): Either<Failure, Coin> =
        request(
            call = coinService.getCoin(coinId),
            mapping = { coinApiModel ->
                coinMapper.mapCoin(coinApiModel)
            }
        )

    override suspend fun getCoinList(): Either<Failure, List<CoinSummary>> =
        request(
            call = coinService.getCoins(),
            mapping = { coinsSummaryApiModel ->
                coinsSummaryApiModel.map { coinSummaryApiModel ->
                    coinMapper.mapCoinSummary(coinSummaryApiModel)
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
                true -> Either.Right(mapping(requireNotNull(response.body())))
                false -> Either.Left(ServerError)
            }
        }.onFailure { throwable ->
            Timber.e(throwable)
        }.getOrElse {
            Either.Left(NetworkingError)
        }
}
package com.ricardaparicio.cryptodemo.features.common.data.datasource

import arrow.core.Either
import com.ricardaparicio.cryptodemo.features.common.data.CoinApiService
import com.ricardaparicio.cryptodemo.features.common.data.mapper.CoinMapper
import retrofit2.Call
import timber.log.Timber
import javax.inject.Inject

class CoinApiDataSource
@Inject constructor(
    private val coinService: CoinApiService,
    private val coinMapper: CoinMapper,
) : CoinRemoteDataSource {

    private fun <T, R> request(
        call: Call<T>,
        mapping: (T) -> R,
    ): Either<Error, R> =
        try {
            val response = call.execute()
            when (response.isSuccessful) {
                true -> Either.Right(mapping(requireNotNull(response.body())))
                false -> Either.Left(ServerError)
            }
        } catch (exception: Throwable) {
            when (exception is NoConnectivityException) {
                true -> Either.Left(NetworkError)
                false -> {
                    Timber.e(exception)
                    Either.Left(ServerError)
                }
            }
        }
}
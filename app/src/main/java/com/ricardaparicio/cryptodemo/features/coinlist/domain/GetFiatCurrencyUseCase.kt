package com.ricardaparicio.cryptodemo.features.coinlist.domain

import arrow.core.Either
import com.ricardaparicio.cryptodemo.core.CoroutineDispatchers
import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.core.usecase.NoParam
import com.ricardaparicio.cryptodemo.core.usecase.UseCase
import com.ricardaparicio.cryptodemo.core.usecase.UseCaseResult
import com.ricardaparicio.cryptodemo.features.common.data.repository.CoinRepository
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import javax.inject.Inject

class GetFiatCurrencyUseCase @Inject constructor(
    private val coinRepository: CoinRepository,
    dispatchers: CoroutineDispatchers,
) : UseCase<NoParam, GetFiatCurrencyUseCase.Result>(dispatchers) {

    data class Result(val currency: FiatCurrency) : UseCaseResult

    override suspend fun doWork(params: NoParam): Either<Failure, Result> =
        coinRepository.getFiatCurrency().map { currency -> Result(currency) }
}
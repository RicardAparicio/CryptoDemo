package com.ricardaparicio.cryptodemo.features.list.domain

import arrow.core.Either
import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.core.usecase.NoResult
import com.ricardaparicio.cryptodemo.core.usecase.UseCase
import com.ricardaparicio.cryptodemo.core.usecase.UseCaseParams
import com.ricardaparicio.cryptodemo.features.common.data.repository.CoinRepository
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import com.ricardaparicio.cryptodemo.features.list.domain.UpdateFiatCurrencyUseCase.Params
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class UpdateFiatCurrencyUseCase @Inject constructor(
    private val coinRepository: CoinRepository
) : UseCase<Params, NoResult>(Dispatchers.IO) {

    data class Params(val currency: FiatCurrency) : UseCaseParams

    override suspend fun doWork(params: Params): Either<Failure, NoResult> =
        coinRepository.updateFiatCurrency(params.currency).map { NoResult }
}
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
package com.ricardaparicio.cryptodemo.features.coinlist.domain

import arrow.core.Either
import com.ricardaparicio.cryptodemo.core.CoroutineDispatchers
import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.core.usecase.UseCase
import com.ricardaparicio.cryptodemo.core.usecase.UseCaseParams
import com.ricardaparicio.cryptodemo.core.usecase.UseCaseResult
import com.ricardaparicio.cryptodemo.features.coinlist.domain.UpdateFiatCurrencyUseCase.Params
import com.ricardaparicio.cryptodemo.features.coinlist.domain.UpdateFiatCurrencyUseCase.Result
import com.ricardaparicio.cryptodemo.features.common.data.repository.CoinRepository
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class UpdateFiatCurrencyUseCase @Inject constructor(
    private val coinRepository: CoinRepository,
    dispatchers: CoroutineDispatchers,
) : UseCase<Params, Result>(dispatchers) {

    data class Params(val currency: FiatCurrency) : UseCaseParams
    data class Result(val currency: FiatCurrency) : UseCaseResult

    override suspend fun doWork(params: Params): Either<Failure, Result> =
        coinRepository.updateFiatCurrency(params.currency).map { currency -> Result(currency) }
}
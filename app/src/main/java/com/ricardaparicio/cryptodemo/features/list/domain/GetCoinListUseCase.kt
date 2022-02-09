package com.ricardaparicio.cryptodemo.features.list.domain

import arrow.core.Either
import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.core.usecase.FlowUseCase
import com.ricardaparicio.cryptodemo.core.usecase.NoParam
import com.ricardaparicio.cryptodemo.core.usecase.UseCaseResult
import com.ricardaparicio.cryptodemo.features.common.data.repository.CoinRepository
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinSummary
import com.ricardaparicio.cryptodemo.features.list.domain.GetCoinListUseCase.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCoinListUseCase @Inject constructor(
    private val coinRepository: CoinRepository
) : FlowUseCase<NoParam, Result>(Dispatchers.IO) {
    data class Result(val coins: List<CoinSummary>) : UseCaseResult

    override suspend fun doWork(params: NoParam): Flow<Either<Failure, Result>> =
        coinRepository.getCoinList().map { coinsResult ->
            coinsResult.map { coins ->
                Result(coins)
            }
        }
}
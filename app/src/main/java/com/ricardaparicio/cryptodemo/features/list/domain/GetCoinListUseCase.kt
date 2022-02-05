package com.ricardaparicio.cryptodemo.features.list.domain

import arrow.core.Either
import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.core.NoParam
import com.ricardaparicio.cryptodemo.core.UseCase
import com.ricardaparicio.cryptodemo.core.UseCaseResult
import com.ricardaparicio.cryptodemo.features.common.data.repository.CoinRepository
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinSummary
import com.ricardaparicio.cryptodemo.features.list.domain.GetCoinListUseCase.Result
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class GetCoinListUseCase @Inject constructor(
    private val coinRepository: CoinRepository
) : UseCase<NoParam, Result>(Dispatchers.IO) {
    data class Result(val coins: List<CoinSummary>) : UseCaseResult

    override suspend fun doWork(params: NoParam): Either<Failure, Result> =
        coinRepository.getCoinList().map { coins ->
            Result(coins)
        }
}
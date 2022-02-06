package com.ricardaparicio.cryptodemo.core

import arrow.core.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class UseCase<P : UseCaseParams, R : UseCaseResult>(
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(params: P): Either<Failure, R> = withContext(dispatcher) { doWork(params) }

    abstract suspend fun doWork(params: P): Either<Failure, R>
}

interface UseCaseParams
interface UseCaseResult

object NoParam : UseCaseParams
object NoResult : UseCaseResult
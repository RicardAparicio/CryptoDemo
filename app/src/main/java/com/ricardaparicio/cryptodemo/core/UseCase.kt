package com.ricardaparicio.cryptodemo.core

import arrow.core.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class UseCase<P : UseCaseParams, R : UseCaseResult, E : FeatureError>(
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun run(params: P): Either<E, R> = withContext(dispatcher) { doWork(params) }

    abstract suspend fun doWork(params: P): Either<E, R>
}

interface UseCaseParams
interface UseCaseResult

object NoParam : UseCaseParams
object NoResult : UseCaseResult
object GenericError : FeatureError
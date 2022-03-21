package com.ricardaparicio.cryptodemo.core.usecase

import arrow.core.Either
import com.ricardaparicio.cryptodemo.core.CoroutineDispatchers
import com.ricardaparicio.cryptodemo.core.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<P : UseCaseParams, R : UseCaseResult>(
    private val dispatchers: CoroutineDispatchers
) {
    operator fun invoke(params: P): Flow<Either<Failure, R>> = doWork(params).flowOn(dispatchers.io)

    abstract fun doWork(params: P): Flow<Either<Failure, R>>
}
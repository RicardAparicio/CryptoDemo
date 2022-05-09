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
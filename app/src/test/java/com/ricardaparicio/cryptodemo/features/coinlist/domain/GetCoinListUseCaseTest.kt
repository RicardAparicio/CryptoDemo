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
import arrow.core.left
import arrow.core.right
import com.ricardaparicio.cryptodemo.core.NetworkingError
import com.ricardaparicio.cryptodemo.core.usecase.NoParam
import com.ricardaparicio.cryptodemo.features.TestCoroutineDispatchers
import com.ricardaparicio.cryptodemo.features.coinsState
import com.ricardaparicio.cryptodemo.features.common.data.repository.CoinRepository
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinListState
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetCoinListUseCaseTest {
    @MockK
    private lateinit var coinRepository: CoinRepository
    private lateinit var getCoinListUseCase: GetCoinListUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getCoinListUseCase = GetCoinListUseCase(coinRepository, TestCoroutineDispatchers)
    }

    @Test
    fun `when UseCase is executed then request coin list from repository`() =
        runTest {
            coEvery { coinRepository.getCoinList() } returns flowOf(coinsState.right())

            getCoinListUseCase(NoParam)

            coVerify(exactly = 1) { coinRepository.getCoinList() }
        }

    @Test
    fun `when Repository result is successful then return Either right as UseCase Result`() =
        runTest {
            val expectedResult = GetCoinListUseCase.Result(coinsState)
            coEvery { coinRepository.getCoinList() } returns flowOf(coinsState.right())

            val result = getCoinListUseCase(NoParam)

            assert(result.first().isRight())
            assert((result.first() as Either.Right).value == expectedResult)
        }

    @Test
    fun `when Repository result is failed then return Either left as Failure`() =
        runTest {
            val expectedResult = NetworkingError
            coEvery { coinRepository.getCoinList() } returns flowOf(expectedResult.left())

            val result = getCoinListUseCase(NoParam)

            assert(result.first().isLeft())
            assert((result.first() as Either.Left).value == expectedResult)
        }
}
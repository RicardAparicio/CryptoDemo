package com.ricardaparicio.cryptodemo.features.coindetail.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.ricardaparicio.cryptodemo.core.NetworkingError
import com.ricardaparicio.cryptodemo.features.COIN_ID
import com.ricardaparicio.cryptodemo.features.TestCoroutineDispatchers
import com.ricardaparicio.cryptodemo.features.coin
import com.ricardaparicio.cryptodemo.features.common.data.repository.CoinRepository
import com.ricardaparicio.cryptodemo.features.common.domain.model.Coin
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinSummary
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetCoinUseCaseTest {
    @MockK
    private lateinit var coinRepository: CoinRepository
    private lateinit var getCoinUseCase: GetCoinUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getCoinUseCase = GetCoinUseCase(coinRepository, TestCoroutineDispatchers)
    }

    @Test
    fun `when UseCase is executed then request coin detail from repository with the same params`() =
        runTest {
            coEvery { coinRepository.getCoin(COIN_ID) } returns coin.right()

            getCoinUseCase(GetCoinUseCase.Params(COIN_ID))

            coVerify(exactly = 1) { coinRepository.getCoin(COIN_ID) }
        }

    @Test
    fun `when Repository result is successful then return Either right as UseCase Result`() =
        runTest {
            val expectedResult = GetCoinUseCase.Result(coin)
            coEvery { coinRepository.getCoin(any()) } returns coin.right()

            val result = getCoinUseCase(GetCoinUseCase.Params(COIN_ID))

            assert(result.isRight())
            assert((result as Either.Right).value == expectedResult)
        }

    @Test
    fun `when Repository result is failed then return Either left as Failure`() =
        runTest {
            val expectedResult = NetworkingError
            coEvery { coinRepository.getCoin(any()) } returns expectedResult.left()

            val result = getCoinUseCase(GetCoinUseCase.Params(COIN_ID))

            assert(result.isLeft())
            assert((result as Either.Left).value == expectedResult)
        }
}
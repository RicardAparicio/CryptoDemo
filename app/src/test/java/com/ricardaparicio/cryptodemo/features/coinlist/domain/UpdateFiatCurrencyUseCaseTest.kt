package com.ricardaparicio.cryptodemo.features.coinlist.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.ricardaparicio.cryptodemo.core.NetworkingError
import com.ricardaparicio.cryptodemo.features.TestCoroutineDispatchers
import com.ricardaparicio.cryptodemo.features.common.data.repository.CoinRepository
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
class UpdateFiatCurrencyUseCaseTest {
    @MockK
    private lateinit var coinRepository: CoinRepository
    private lateinit var updateFiatCurrencyUseCase: UpdateFiatCurrencyUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        updateFiatCurrencyUseCase =
            UpdateFiatCurrencyUseCase(coinRepository, TestCoroutineDispatchers)
    }

    @Test
    fun `when UseCase is executed then request current fiat currency from repository with the same params`() =
        runTest {
            val fiatCurrencyParams = fiatCurrency
            coEvery { coinRepository.updateFiatCurrency(fiatCurrencyParams) } returns fiatCurrency.right()

            updateFiatCurrencyUseCase(UpdateFiatCurrencyUseCase.Params(fiatCurrencyParams))

            coVerify(exactly = 1) { coinRepository.updateFiatCurrency(fiatCurrencyParams) }
        }

    @Test
    fun `when Repository result is successful then return Either right as UseCase Result`() =
        runTest {
            val expectedResult = UpdateFiatCurrencyUseCase.Result(fiatCurrency)
            coEvery { coinRepository.updateFiatCurrency(any()) } returns fiatCurrency.right()

            val result = updateFiatCurrencyUseCase(UpdateFiatCurrencyUseCase.Params(fiatCurrency))

            assert(result.isRight())
            assert((result as Either.Right).value == expectedResult)
        }

    @Test
    fun `when Repository result is failed then return Either left as Failure`() =
        runTest {
            val expectedResult = NetworkingError
            coEvery { coinRepository.updateFiatCurrency(any()) } returns expectedResult.left()

            val result = updateFiatCurrencyUseCase(UpdateFiatCurrencyUseCase.Params(fiatCurrency))

            assert(result.isLeft())
            assert((result as Either.Left).value == expectedResult)
        }

    companion object {
        private val fiatCurrency get() = FiatCurrency.Eur
    }
}
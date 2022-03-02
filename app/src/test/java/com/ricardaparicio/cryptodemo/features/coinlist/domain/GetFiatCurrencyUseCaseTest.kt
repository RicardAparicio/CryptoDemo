package com.ricardaparicio.cryptodemo.features.coinlist.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.ricardaparicio.cryptodemo.core.NetworkingError
import com.ricardaparicio.cryptodemo.core.usecase.NoParam
import com.ricardaparicio.cryptodemo.features.common.data.repository.CoinRepository
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetFiatCurrencyUseCaseTest {
    @MockK
    private lateinit var coinRepository: CoinRepository
    private lateinit var getFiatCurrencyUseCase: GetFiatCurrencyUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getFiatCurrencyUseCase = GetFiatCurrencyUseCase(coinRepository)
    }

    @Test
    fun `when UseCase is executed then request current fiat currency from repository`() =
        runBlocking {
            coEvery { coinRepository.getFiatCurrency() } returns fiatCurrency.right()

            getFiatCurrencyUseCase(NoParam)

            coVerify(exactly = 1) { coinRepository.getFiatCurrency() }
        }

    @Test
    fun `when Repository result is successful then return Either right as UseCase Result`() =
        runBlocking {
            val expectedResult = GetFiatCurrencyUseCase.Result(fiatCurrency)
            coEvery { coinRepository.getFiatCurrency() } returns fiatCurrency.right()

            val result = getFiatCurrencyUseCase(NoParam)

            assert(result.isRight())
            assert((result as Either.Right).value == expectedResult)
        }

    @Test
    fun `when Repository result is failed then return Either left as Failure`() = runBlocking {
        val expectedResult = NetworkingError
        coEvery { coinRepository.getFiatCurrency() } returns expectedResult.left()

        val result = getFiatCurrencyUseCase(NoParam)

        assert(result.isLeft())
        assert((result as Either.Left).value == expectedResult)
    }

    companion object {
        private val fiatCurrency get() = FiatCurrency.Eur
    }
}
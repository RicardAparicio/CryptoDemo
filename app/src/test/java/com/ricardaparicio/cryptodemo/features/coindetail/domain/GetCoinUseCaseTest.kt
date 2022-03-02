package com.ricardaparicio.cryptodemo.features.coindetail.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.ricardaparicio.cryptodemo.core.NetworkingError
import com.ricardaparicio.cryptodemo.features.common.data.repository.CoinRepository
import com.ricardaparicio.cryptodemo.features.common.domain.model.Coin
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinSummary
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetCoinUseCaseTest {
    @MockK
    private lateinit var coinRepository: CoinRepository
    private lateinit var getCoinUseCase: GetCoinUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getCoinUseCase = GetCoinUseCase(coinRepository)
    }

    @Test
    fun `when UseCase is executed then request coin detail from repository with the same params`() = runBlocking {
        coEvery { coinRepository.getCoin(COIN_ID) } returns coin.right()

        getCoinUseCase(GetCoinUseCase.Params(COIN_ID))

        coVerify(exactly = 1) { coinRepository.getCoin(COIN_ID) }
    }

    @Test
    fun `when Repository result is successful then return Either right as UseCase Result`() =
        runBlocking {
            val expectedResult = GetCoinUseCase.Result(coin)
            coEvery { coinRepository.getCoin(any()) } returns coin.right()

            val result = getCoinUseCase(GetCoinUseCase.Params(COIN_ID))

            assert(result.isRight())
            assert((result as Either.Right).value == expectedResult)
        }

    @Test
    fun `when Repository result is failed then return Either left as Failure`() = runBlocking {
        val expectedResult = NetworkingError
        coEvery { coinRepository.getCoin(any()) } returns expectedResult.left()

        val result = getCoinUseCase(GetCoinUseCase.Params(COIN_ID))

        assert(result.isLeft())
        assert((result as Either.Left).value == expectedResult)
    }

    companion object {
        private const val COIN_ID = "btc"
        private val coin = Coin(
            coinSummary = CoinSummary(
                id = "btc",
                symbol = "BTC",
                name = "bitcoin",
                image = "",
                price = 34.000f,
                marketCapRank = 1,
                fiatCurrency = FiatCurrency.Eur
            ),
            description = "Satoshi Nakamoto",
            ath = 65000f,
            marketCap = 800000000f,
            priceChange24h = 2000f,
            priceChangePercentage24h = 3f,
        )
    }
}
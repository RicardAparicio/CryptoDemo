package com.ricardaparicio.cryptodemo.features.common.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.ricardaparicio.cryptodemo.core.LocalError
import com.ricardaparicio.cryptodemo.core.NetworkingError
import com.ricardaparicio.cryptodemo.features.COIN_ID
import com.ricardaparicio.cryptodemo.features.coin
import com.ricardaparicio.cryptodemo.features.common.data.datasource.CoinLocalDataSource
import com.ricardaparicio.cryptodemo.features.common.data.datasource.CoinRemoteDataSource
import com.ricardaparicio.cryptodemo.features.fiatCurrency
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class CoinRepositoryTest {
    @MockK
    private lateinit var localDataSource: CoinLocalDataSource

    @MockK
    private lateinit var remoteDataSource: CoinRemoteDataSource
    private lateinit var coinRepository: CoinRepository

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        coinRepository = CoinRepository(remoteDataSource, localDataSource)
    }

    @Test
    fun `when fiat currency is requested then return the current one from local DataSource`() =
        runTest {
            coEvery { localDataSource.fiatCurrencyFlow() } returns flowOf(fiatCurrency.right())

            val result = coinRepository.getFiatCurrency()

            coVerify(exactly = 1) { localDataSource.fiatCurrencyFlow() }
            assert(result.isRight())
            assert((result as Either.Right).value == fiatCurrency)
        }

    @Test
    fun `when fiat currency is requested and there was an error then return that error`() =
        runTest {
            coEvery { localDataSource.fiatCurrencyFlow() } returns flowOf(LocalError.left())

            val result = coinRepository.getFiatCurrency()

            assert(result.isLeft())
            assert((result as Either.Left).value == LocalError)
        }

    @Test
    fun `when fiat currency is requested for update then update it with local DataSource and returns the current one`() =
        runTest {
            coEvery { localDataSource.updateFiatCurrency(fiatCurrency) } returns Unit.right()
            coEvery { localDataSource.fiatCurrencyFlow() } returns flowOf(fiatCurrency.right())

            val result = coinRepository.updateFiatCurrency(fiatCurrency)

            coVerify(exactly = 1) { localDataSource.updateFiatCurrency(fiatCurrency) }
            coVerify(exactly = 1) { localDataSource.fiatCurrencyFlow() }
            assert(result.isRight())
            assert((result as Either.Right).value == fiatCurrency)
        }

    @Test
    fun `when fiat currency is requested for update and there was an error updating then return that error`() =
        runTest {
            coEvery { localDataSource.updateFiatCurrency(fiatCurrency) } returns LocalError.left()

            val result = coinRepository.updateFiatCurrency(fiatCurrency)

            coVerify(exactly = 1) { localDataSource.updateFiatCurrency(fiatCurrency) }
            assert(result.isLeft())
            assert((result as Either.Left).value == LocalError)
        }

    @Test
    fun `when fiat currency is requested for update and there was an error returning the current fiat currency then return that error`() =
        runTest {
            coEvery { localDataSource.updateFiatCurrency(fiatCurrency) } returns Unit.right()
            coEvery { localDataSource.fiatCurrencyFlow() } returns flowOf(LocalError.left())

            val result = coinRepository.updateFiatCurrency(fiatCurrency)

            coVerify(exactly = 1) { localDataSource.updateFiatCurrency(fiatCurrency) }
            coVerify(exactly = 1) { localDataSource.fiatCurrencyFlow() }
            assert(result.isLeft())
            assert((result as Either.Left).value == LocalError)
        }

    @Test
    fun `when coin is requested then return it from remote Datasource with the current fiat currency`() =
        runTest {
            coEvery { remoteDataSource.getCoin(COIN_ID, fiatCurrency) } returns coin.right()
            coEvery { localDataSource.fiatCurrencyFlow() } returns flowOf(fiatCurrency.right())

            val result = coinRepository.getCoin(COIN_ID)

            coVerify(exactly = 1) { localDataSource.fiatCurrencyFlow() }
            coVerify(exactly = 1) { remoteDataSource.getCoin(COIN_ID, fiatCurrency) }
            assert(result.isRight())
            assert((result as Either.Right).value == coin)
        }

    @Test
    fun `when coin is requested and there was an error retrieving fiat currency from local then return that error`() =
        runTest {
            coEvery { remoteDataSource.getCoin(COIN_ID, fiatCurrency) } returns coin.right()
            coEvery { localDataSource.fiatCurrencyFlow() } returns flowOf(LocalError.left())

            val result = coinRepository.getCoin(COIN_ID)

            coVerify(exactly = 1) { localDataSource.fiatCurrencyFlow() }
            assert(result.isLeft())
            assert((result as Either.Left).value == LocalError)
        }

    @Test
    fun `when coin is requested and there was an error fetching it from remote then return that error`() =
        runTest {
            coEvery { remoteDataSource.getCoin(COIN_ID, fiatCurrency) } returns NetworkingError.left()
            coEvery { localDataSource.fiatCurrencyFlow() } returns flowOf(fiatCurrency.right())

            val result = coinRepository.getCoin(COIN_ID)

            coVerify(exactly = 1) { localDataSource.fiatCurrencyFlow() }
            coVerify(exactly = 1) { remoteDataSource.getCoin(COIN_ID, fiatCurrency) }

            assert(result.isLeft())
            assert((result as Either.Left).value == NetworkingError)
        }
}
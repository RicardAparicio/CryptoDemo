package com.ricardaparicio.cryptodemo.features.coinlist.presentation.viewmodel

import arrow.core.left
import arrow.core.right
import com.ricardaparicio.cryptodemo.core.LocalError
import com.ricardaparicio.cryptodemo.core.NetworkingError
import com.ricardaparicio.cryptodemo.core.Reducer
import com.ricardaparicio.cryptodemo.features.TestCoroutineDispatchers
import com.ricardaparicio.cryptodemo.features.coinSummary
import com.ricardaparicio.cryptodemo.features.coinlist.domain.GetCoinListUseCase
import com.ricardaparicio.cryptodemo.features.coinlist.domain.GetFiatCurrencyUseCase
import com.ricardaparicio.cryptodemo.features.coinlist.domain.GetFiatCurrencyUseCase.Result
import com.ricardaparicio.cryptodemo.features.coinlist.domain.UpdateFiatCurrencyUseCase
import com.ricardaparicio.cryptodemo.features.coinlist.presentation.reducer.CoinListUiAction
import com.ricardaparicio.cryptodemo.features.coinlist.presentation.reducer.CoinListUiAction.*
import com.ricardaparicio.cryptodemo.features.coinlist.presentation.ui.CoinListUiState
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinListState
import com.ricardaparicio.cryptodemo.features.common.ui.viewmodel.ContentLoadingUiAction
import com.ricardaparicio.cryptodemo.features.fiatCurrency
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CoinListViewModelTest {
    @MockK
    private lateinit var getCoinListUseCase: GetCoinListUseCase
    @MockK
    private lateinit var getFiatCurrencyUseCase: GetFiatCurrencyUseCase
    @MockK
    private lateinit var updateFiatCurrencyUseCase: UpdateFiatCurrencyUseCase
    @MockK
    private lateinit var reducer: Reducer<CoinListUiState, CoinListUiAction>

    private lateinit var viewModel: CoinListViewModel

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(TestCoroutineDispatchers.main)
    }

    @After
    fun onAfter() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when viewModel is created get current fiat currency with coins list`() =
        runTest {
            val updateFiatCurrencyResult = Result(fiatCurrency)
            val coins = listOf(coinSummary)
            coEvery { getFiatCurrencyUseCase(any()) } returns updateFiatCurrencyResult.right()
            coEvery { getCoinListUseCase(any()) } returns flowOf(
                GetCoinListUseCase.Result(CoinListState.Loading).right(),
                GetCoinListUseCase.Result(CoinListState.Coins(coins)).right()
            )
            coEvery { reducer.reduce(any(), any()) } returns CoinListUiState()

            viewModel = CoinListViewModel(
                getCoinListUseCase,
                getFiatCurrencyUseCase,
                updateFiatCurrencyUseCase,
                reducer
            )

            coVerify(exactly = 1) {
                getCoinListUseCase(any())
                getFiatCurrencyUseCase(any())
            }
            coVerifySequence {
                reducer.reduce(any(), UpdateFiatCurrency(updateFiatCurrencyResult.currency))
                reducer.reduce(any(), UpdateContentLoading(ContentLoadingUiAction.Loading))
                reducer.reduce(any(), NewCoins(coins))
                reducer.reduce(any(), UpdateContentLoading(ContentLoadingUiAction.Success))
            }
        }

    @Test
    fun `when viewModel is created an there was an error getting current fiat currency then render that error`() =
        runTest {
            coEvery { getFiatCurrencyUseCase(any()) } returns LocalError.left()
            coEvery { getCoinListUseCase(any()) } returns flowOf()
            coEvery { reducer.reduce(any(), any()) } returns CoinListUiState()

            viewModel = CoinListViewModel(
                getCoinListUseCase,
                getFiatCurrencyUseCase,
                updateFiatCurrencyUseCase,
                reducer
            )

            coVerify(exactly = 1) {
                getFiatCurrencyUseCase(any())
            }
            coVerify(exactly = 1) {
                reducer.reduce(any(), UpdateContentLoading(ContentLoadingUiAction.Error(LocalError)))
            }
        }

    @Test
    fun `when viewModel is created an there was an error fetching coins list then render that error`() =
        runTest {
            coEvery { getFiatCurrencyUseCase(any()) } returns Result(fiatCurrency).right()
            coEvery { getCoinListUseCase(any()) } returns flowOf(NetworkingError.left())
            coEvery { reducer.reduce(any(), any()) } returns CoinListUiState()

            viewModel = CoinListViewModel(
                getCoinListUseCase,
                getFiatCurrencyUseCase,
                updateFiatCurrencyUseCase,
                reducer
            )

            coVerify(exactly = 1) {
                getCoinListUseCase(any())
            }
            coVerify {
                reducer.reduce(any(), UpdateContentLoading(ContentLoadingUiAction.Error(NetworkingError)))
            }
        }

    @Test
    fun `when fiat currency is clicked then update fiat currency and notify it the reducer`() =
        runTest {
            val updateFiatCurrencyResult = UpdateFiatCurrencyUseCase.Result(fiatCurrency)
            coEvery { reducer.reduce(any(), any()) } returns CoinListUiState()
            coEvery { updateFiatCurrencyUseCase(any()) } returns updateFiatCurrencyResult.right()

            viewModel = CoinListViewModel(
                getCoinListUseCase,
                getFiatCurrencyUseCase,
                updateFiatCurrencyUseCase,
                reducer
            )
            viewModel.onFiatCurrencyClicked(fiatCurrency)

            coVerify(exactly = 1) {
                updateFiatCurrencyUseCase(any())
                reducer.reduce(any(), UpdateFiatCurrency(updateFiatCurrencyResult.currency))
            }
        }

    @Test
    fun `when fiat currency is clicked then was an error updating fiat currency then render that error`() =
        runTest {
            coEvery { reducer.reduce(any(), any()) } returns CoinListUiState()
            coEvery { updateFiatCurrencyUseCase(any()) } returns LocalError.left()

            viewModel = CoinListViewModel(
                getCoinListUseCase,
                getFiatCurrencyUseCase,
                updateFiatCurrencyUseCase,
                reducer
            )
            viewModel.onFiatCurrencyClicked(fiatCurrency)

            coVerify(exactly = 1) {
                updateFiatCurrencyUseCase(any())
                reducer.reduce(any(), UpdateContentLoading(ContentLoadingUiAction.Error(LocalError)))
            }
        }
}
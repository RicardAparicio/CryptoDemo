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
package com.ricardaparicio.cryptodemo.features.coindetail.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import arrow.core.left
import arrow.core.right
import com.ricardaparicio.cryptodemo.core.NetworkingError
import com.ricardaparicio.cryptodemo.core.Reducer
import com.ricardaparicio.cryptodemo.features.COIN_ID
import com.ricardaparicio.cryptodemo.features.TestCoroutineDispatchers
import com.ricardaparicio.cryptodemo.features.coin
import com.ricardaparicio.cryptodemo.features.coindetail.domain.GetCoinUseCase
import com.ricardaparicio.cryptodemo.features.coindetail.presentation.reducer.CoinDetailUiAction
import com.ricardaparicio.cryptodemo.features.coindetail.presentation.ui.CoinDetailUiState
import com.ricardaparicio.cryptodemo.features.common.ui.viewmodel.ContentLoadingUiAction
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CoinDetailViewModelTest {
    @MockK
    private lateinit var getCoinUseCase: GetCoinUseCase
    @MockK
    private lateinit var reducer: Reducer<CoinDetailUiState, CoinDetailUiAction>
    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: CoinDetailViewModel

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
    fun `when viewModel is created fetch coin by id`() =
        runTest {
            val coinResult = GetCoinUseCase.Result(coin)
            coEvery { getCoinUseCase(any()) } returns coinResult.right()
            coEvery { reducer.reduce(any(), any()) } returns CoinDetailUiState()
            coEvery { savedStateHandle.get<String>(any()) } returns COIN_ID

            viewModel = CoinDetailViewModel(getCoinUseCase, reducer, savedStateHandle)

            coVerify(exactly = 1) {
                getCoinUseCase(any())
                savedStateHandle.get<String>(any())
            }
            coVerify(exactly = 3) {
                reducer.reduce(any(), any())
            }
            coVerifySequence {
                reducer.reduce(any(), CoinDetailUiAction.UpdateContentLoading(ContentLoadingUiAction.Loading))
                reducer.reduce(any(), CoinDetailUiAction.NewCoin(coinResult.coin))
                reducer.reduce(any(), CoinDetailUiAction.UpdateContentLoading(ContentLoadingUiAction.Success))
            }
        }

    @Test
    fun `when viewModel is created and there was an error fetching coin then render that error`() =
        runTest {
            coEvery { getCoinUseCase(any()) } returns NetworkingError.left()
            coEvery { reducer.reduce(any(), any()) } returns CoinDetailUiState()
            coEvery { savedStateHandle.get<String>(any()) } returns COIN_ID

            viewModel = CoinDetailViewModel(getCoinUseCase, reducer, savedStateHandle)

            coVerifySequence {
                reducer.reduce(any(), CoinDetailUiAction.UpdateContentLoading(ContentLoadingUiAction.Loading))
                reducer.reduce(any(), CoinDetailUiAction.UpdateContentLoading(ContentLoadingUiAction.Error(NetworkingError)))
            }
        }

    @Test
    fun `when onDismissDialogRequested is called then render CloseError`() =
        runTest {
            coEvery { getCoinUseCase(any()) } returns NetworkingError.left()
            coEvery { reducer.reduce(any(), any()) } returns CoinDetailUiState()
            coEvery { savedStateHandle.get<String>(any()) } returns COIN_ID

            viewModel = CoinDetailViewModel(getCoinUseCase, reducer, savedStateHandle)
            viewModel.onDismissDialogRequested()

            coVerify {
                reducer.reduce(any(), CoinDetailUiAction.UpdateContentLoading(ContentLoadingUiAction.CloseError))
            }
        }
}
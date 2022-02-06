package com.ricardaparicio.cryptodemo.features.detail.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricardaparicio.cryptodemo.core.Reducer
import com.ricardaparicio.cryptodemo.core.navigation.NavArg
import com.ricardaparicio.cryptodemo.core.util.doNothing
import com.ricardaparicio.cryptodemo.features.detail.domain.GetCoinUseCase
import com.ricardaparicio.cryptodemo.features.detail.ui.CoinDetailUiState
import com.ricardaparicio.cryptodemo.features.detail.ui.reducer.CoinDetailUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val getCoinUseCase: GetCoinUseCase,
    private val reducer: Reducer<CoinDetailUiState, CoinDetailUiAction>,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var uiState by mutableStateOf(CoinDetailUiState())
        private set

    private val coinId: String
        get() = requireNotNull(
            savedStateHandle.get<String>(NavArg.CoinId.key)
        )

    init {
        fetchCoin()
    }

    private fun fetchCoin() =
        viewModelScope.launch {
            getCoinUseCase(GetCoinUseCase.Params(coinId))
                .fold(
                    {
                        doNothing()
                    },
                    { result ->
                        reduce(
                            CoinDetailUiAction.NewCoin(result.coin)
                        )
                    }
                )
        }

    private fun reduce(action: CoinDetailUiAction) {
        uiState = reducer.reduce(uiState, action)
    }

}
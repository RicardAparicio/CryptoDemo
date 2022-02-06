package com.ricardaparicio.cryptodemo.features.list.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricardaparicio.cryptodemo.core.NoParam
import com.ricardaparicio.cryptodemo.core.Reducer
import com.ricardaparicio.cryptodemo.core.util.doNothing
import com.ricardaparicio.cryptodemo.features.list.domain.GetCoinListUseCase
import com.ricardaparicio.cryptodemo.features.list.ui.CoinListUiState
import com.ricardaparicio.cryptodemo.features.list.ui.reducer.CoinListUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel
@Inject constructor(
    private val getCoinListUseCase: GetCoinListUseCase,
    private val reducer: Reducer<CoinListUiState, CoinListUiAction>,
) : ViewModel() {

    var uiState by mutableStateOf(CoinListUiState())
        private set

    init {
        fetchCoins()
    }

    private fun fetchCoins() =
        viewModelScope.launch {
            getCoinListUseCase(NoParam)
                .fold(
                    {
                        doNothing()
                    },
                    { result ->
                        reduce(
                            CoinListUiAction.Coins(result.coins)
                        )
                    }
                )
        }

    private fun reduce(action: CoinListUiAction) {
        uiState = reducer.reduce(uiState, action)
    }
}
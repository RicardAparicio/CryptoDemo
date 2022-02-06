package com.ricardaparicio.cryptodemo.features.list.ui.viewmodel

import com.ricardaparicio.cryptodemo.core.Reducer
import com.ricardaparicio.cryptodemo.features.list.ui.CoinListUiState
import com.ricardaparicio.cryptodemo.features.list.ui.reducer.CoinListReducer
import com.ricardaparicio.cryptodemo.features.list.ui.reducer.CoinListUiAction
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface CoinListViewModelModule {
    @Binds
    fun provideCoinListReducer(reducer: CoinListReducer): Reducer<CoinListUiState, CoinListUiAction>
}
package com.ricardaparicio.cryptodemo.features.common.ui.viewmodel

import com.ricardaparicio.cryptodemo.core.NetworkingError
import com.ricardaparicio.cryptodemo.features.common.ui.ContentLoadingUiState
import com.ricardaparicio.cryptodemo.features.common.ui.model.AlertErrorUiModel
import org.junit.Before
import org.junit.Test

class ContentLoadingReducerTest {

    private lateinit var reducer : ContentLoadingReducer

    @Before
    fun onBefore() {
        reducer = ContentLoadingReducer()
    }

    @Test
    fun `when Loading action is requested then update the state properly`() {
        val updatedState = reducer.reduce(ContentLoadingUiState(), ContentLoadingUiAction.Loading)

        assert(updatedState.loading)
    }

    @Test
    fun `when Success action is requested then update the state properly`() {
        val updatedState = reducer.reduce(ContentLoadingUiState(), ContentLoadingUiAction.Success)

        assert(updatedState.error == null)
        assert(!updatedState.loading)
    }

    @Test
    fun `when CloseError action is requested then update the state properly`() {
        val updatedState = reducer.reduce(ContentLoadingUiState(), ContentLoadingUiAction.CloseError)

        assert(updatedState.error == null)
    }

    @Test
    fun `when Error action is requested then update the state properly`() {
        val updatedState = reducer.reduce(ContentLoadingUiState(), ContentLoadingUiAction.Error(NetworkingError))

        assert(updatedState.error == AlertErrorUiModel.from(NetworkingError))
    }
}
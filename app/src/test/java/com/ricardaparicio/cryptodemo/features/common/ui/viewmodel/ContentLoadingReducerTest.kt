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
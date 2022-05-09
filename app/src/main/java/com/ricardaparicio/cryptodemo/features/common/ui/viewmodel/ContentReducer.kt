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

import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.core.Reducer
import com.ricardaparicio.cryptodemo.core.UiAction
import com.ricardaparicio.cryptodemo.features.common.ui.ContentLoadingUiState
import com.ricardaparicio.cryptodemo.features.common.ui.model.AlertErrorUiModel
import javax.inject.Inject

sealed class ContentLoadingUiAction : UiAction {
    object Loading : ContentLoadingUiAction()
    object Success : ContentLoadingUiAction()
    object CloseError : ContentLoadingUiAction()
    data class Error(val failure: Failure) : ContentLoadingUiAction()
}

class ContentLoadingReducer @Inject constructor() : Reducer<ContentLoadingUiState, ContentLoadingUiAction> {

    override val reduce: (ContentLoadingUiState, ContentLoadingUiAction) -> ContentLoadingUiState =
        { state, action ->
            when (action) {
                ContentLoadingUiAction.Loading -> {
                    state.copy(
                        loading = true,
                    )
                }
                ContentLoadingUiAction.CloseError -> {
                    state.copy(
                        error = null
                    )
                }
                is ContentLoadingUiAction.Error -> {
                    state.copy(
                        error = AlertErrorUiModel.from(action.failure),
                        loading = false,
                    )
                }
                ContentLoadingUiAction.Success -> {
                    state.copy(
                        error = null,
                        loading = false,
                    )
                }
            }
        }
}

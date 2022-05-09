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
package com.ricardaparicio.cryptodemo.features.coindetail.presentation.ui

import com.ricardaparicio.cryptodemo.features.common.ui.model.CoinSummaryUiModel
import com.ricardaparicio.cryptodemo.features.common.ui.ContentLoadingUiState


data class CoinDetailUiState(
    val coinSummary: CoinSummaryUiModel = CoinSummaryUiModel(),
    val description: String = "",
    val ath: String = "",
    val marketCap: String = "",
    val priceChange24h: String = "",
    val priceChangePercentage24h: String = "",
    val contentLoadingUiState: ContentLoadingUiState = ContentLoadingUiState.empty,
)

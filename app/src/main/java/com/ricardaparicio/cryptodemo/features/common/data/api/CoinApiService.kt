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
package com.ricardaparicio.cryptodemo.features.common.data.api

import com.ricardaparicio.cryptodemo.features.common.data.api.model.CoinApiModel
import com.ricardaparicio.cryptodemo.features.common.data.api.model.CoinSummaryApiModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinApiService {
    @GET("coins/markets")
    fun getCoins(
        @Query("vs_currency") currency: String,
        @Query("per_page") itemsPerPage: Int = 20,
        @Query("sparkline") sparkLine: Boolean = false,
    ): Call<List<CoinSummaryApiModel>>

    @GET("coins/{id}")
    fun getCoin(@Path("id") coinId: String): Call<CoinApiModel>
}
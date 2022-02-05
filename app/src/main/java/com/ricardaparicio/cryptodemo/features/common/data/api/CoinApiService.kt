package com.ricardaparicio.cryptodemo.features.common.data.api

import com.ricardaparicio.cryptodemo.features.common.data.model.CoinApiModel
import com.ricardaparicio.cryptodemo.features.common.data.model.CoinSummaryApiModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinApiService {
    @GET("coins/markets")
    suspend fun getCoins(
        @Query("per_page") itemsPerPage: Int = 20,
        @Query("vs_currency") currency: String = "eur",
        @Query("sparkline") sparkLine: Boolean = false,
    ): Call<List<CoinSummaryApiModel>>

    @GET("coins/{id}")
    suspend fun getCoin(@Path("id") coinId: String): Call<CoinApiModel>
}
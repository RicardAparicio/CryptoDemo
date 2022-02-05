package com.ricardaparicio.cryptodemo.features.common.data

import com.ricardaparicio.cryptodemo.features.common.data.model.CoinApiModel
import com.ricardaparicio.cryptodemo.features.common.data.model.CoinSummaryApiModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinApiService {
    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") currency: String,
        @Query("order") order: String,
        @Query("per_page") itemsPerPage: Int,
        @Query("page") page: Int,
        @Query("sparkline") sparkLine: Boolean,
    ): Call<List<CoinSummaryApiModel>>

    @GET("coins/{id}")
    suspend fun getCoin(@Path("id") coinId: String): Call<CoinApiModel>
}
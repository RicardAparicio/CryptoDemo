package com.ricardaparicio.cryptodemo.core.di

import com.ricardaparicio.cryptodemo.core.networking.loggingInterceptor
import com.ricardaparicio.cryptodemo.features.common.data.CoinApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    fun provideCoinService(okHttpClient: OkHttpClient): CoinApiService =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(CoinApiService::class.java)
}

private const val BASE_URL = "https://api.coingecko.com/api/v3"
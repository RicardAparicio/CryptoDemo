package com.ricardaparicio.cryptodemo.features.common.data.datasource

import com.ricardaparicio.cryptodemo.features.common.data.api.model.CoinApiModel
import com.ricardaparicio.cryptodemo.features.common.data.api.model.CoinSummaryApiModel
import com.ricardaparicio.cryptodemo.features.common.domain.model.Coin
import com.ricardaparicio.cryptodemo.features.common.domain.model.CoinSummary
import javax.inject.Inject

class CoinMapper @Inject constructor() {

    fun mapCoinSummary(coinSummaryApiModel: CoinSummaryApiModel): CoinSummary =
        CoinSummary(
            id = coinSummaryApiModel.id,
            symbol = coinSummaryApiModel.symbol,
            name = coinSummaryApiModel.name,
            image = coinSummaryApiModel.image,
            price = coinSummaryApiModel.current_price,
            marketCapRank = coinSummaryApiModel.market_cap_rank,
        )

    fun mapCoin(coinApiModel: CoinApiModel): Coin =
        Coin(
            coinSummary = CoinSummary(
                id = coinApiModel.id,
                symbol = coinApiModel.symbol,
                name = coinApiModel.name,
                image = coinApiModel.image.large,
                price = coinApiModel.market_data.current_price.eur,
                marketCapRank = coinApiModel.market_cap_rank,
            ),
            description = coinApiModel.description.es,
            ath = coinApiModel.market_data.ath.eur,
            marketCap = coinApiModel.market_data.market_cap.eur,
            priceChange24h = coinApiModel.market_data.price_change_24h,
            priceChangePercentage24h = coinApiModel.market_data.price_change_percentage_24h,
        )
}
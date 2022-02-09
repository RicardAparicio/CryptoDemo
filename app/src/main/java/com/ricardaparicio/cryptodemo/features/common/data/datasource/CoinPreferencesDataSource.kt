package com.ricardaparicio.cryptodemo.features.common.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import arrow.core.Either
import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.core.LocalError
import com.ricardaparicio.cryptodemo.features.common.domain.model.FiatCurrency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class CoinPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : CoinLocalDataSource {

    override fun fiatCurrencyFlow(): Flow<Either<Failure, FiatCurrency>> =
        dataStore.data.map { preferences ->
            runSafety {
                FiatCurrency.valueOf(preferences[Keys.FIAT_CURRENCY] ?: FiatCurrency.Eur.name)
            }
        }

    override suspend fun updateFiatCurrency(currency: FiatCurrency): Either<Failure, Unit> =
        runSafety {
            dataStore.edit { preferences ->
                preferences[Keys.FIAT_CURRENCY] = currency.name
            }
        }

    private suspend fun <T : Any> runSafety(block: suspend () -> T): Either<Failure, T> =
        runCatching {
            Either.Right(block())
        }.onFailure { error ->
            Timber.e(error)
        }.getOrElse {
            Either.Left(LocalError)
        }

    private object Keys {
        val FIAT_CURRENCY = stringPreferencesKey("fiat_currency")
    }
}
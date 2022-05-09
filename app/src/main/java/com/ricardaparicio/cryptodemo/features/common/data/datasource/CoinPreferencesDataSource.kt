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
package com.ricardaparicio.cryptodemo.features.common.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import arrow.core.Either
import arrow.core.left
import arrow.core.right
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
            wrapEither {
                FiatCurrency.valueOf(preferences[Keys.FIAT_CURRENCY] ?: FiatCurrency.Eur.name)
            }
        }

    override suspend fun updateFiatCurrency(currency: FiatCurrency): Either<Failure, Unit> =
        wrapEither {
            dataStore.edit { preferences ->
                preferences[Keys.FIAT_CURRENCY] = currency.name
            }
        }

    private suspend fun <T : Any> wrapEither(block: suspend () -> T): Either<Failure, T> =
        runCatching {
            block().right()
        }.onFailure { error ->
            Timber.e(error)
        }.getOrElse {
            LocalError.left()
        }

    private object Keys {
        val FIAT_CURRENCY = stringPreferencesKey("fiat_currency")
    }
}
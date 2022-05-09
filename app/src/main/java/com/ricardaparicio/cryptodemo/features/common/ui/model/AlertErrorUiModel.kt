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
package com.ricardaparicio.cryptodemo.features.common.ui.model

import android.support.annotation.StringRes
import com.ricardaparicio.cryptodemo.R
import com.ricardaparicio.cryptodemo.core.Failure
import com.ricardaparicio.cryptodemo.core.LocalError
import com.ricardaparicio.cryptodemo.core.NetworkingError
import com.ricardaparicio.cryptodemo.core.ServerError

enum class AlertErrorUiModel(
    @StringRes val errorTitle: Int,
    @StringRes val errorSubtitle: Int
) {
    Networking(
        R.string.alert_error_title,
        R.string.alert_network_error_subtitle
    ),
    Server(
        R.string.alert_error_title,
        R.string.alert_server_error_subtitle
    ),
    Internal(
        R.string.alert_error_title,
        R.string.alert_internal_error_subtitle
    );

    companion object {
        fun from(failure: Failure): AlertErrorUiModel =
            when (failure) {
                LocalError -> Internal
                NetworkingError -> Networking
                ServerError -> Server
            }
    }
}
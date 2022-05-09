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
package com.ricardaparicio.cryptodemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ricardaparicio.cryptodemo.core.navigation.NavRoute
import com.ricardaparicio.cryptodemo.features.coindetail.presentation.ui.CoinDetailScreen
import com.ricardaparicio.cryptodemo.features.coinlist.presentation.ui.CoinListScreen
import com.ricardaparicio.cryptodemo.theme.CryptoDemoTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptoDemoTheme {
                NavigationHost()
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.CoinList.destination,
    ) {
        composable(
            route = NavRoute.CoinList.destination,
        ) {
            CoinListScreen { coinSummary ->
                navController.navigate(
                    NavRoute.CoinDetail.route(coinSummary.id)
                )
            }
        }
        composable(
            route = NavRoute.CoinDetail.destination,
            arguments = NavRoute.CoinDetail.args,
        ) {
            CoinDetailScreen {
                navController.popBackStack()
            }
        }
    }
}


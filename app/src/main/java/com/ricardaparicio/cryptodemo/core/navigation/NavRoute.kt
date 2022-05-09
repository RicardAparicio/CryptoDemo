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
package com.ricardaparicio.cryptodemo.core.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class NavRoute(
    private val baseRoute: String,
    private val navArgs: List<NavArg> = emptyList(),
) {
    object CoinList : NavRoute("coinList")
    object CoinDetail : NavRoute("coinDetail", listOf(NavArg.CoinId))

    val args: List<NamedNavArgument> = navArgs.map { navArg ->
        navArgument(navArg.key) {
            type = navArg.type
        }
    }

    fun route(vararg args: String = emptyArray()): String =
        buildList {
            add(baseRoute)
            addAll(args)
        }.joinToString("/")

    val destination: String = run {
        val argKeys = navArgs.map { navArg -> "{${navArg.key}}" }
        buildList {
            add(baseRoute)
            addAll(argKeys)
        }.joinToString("/")
    }
}

enum class NavArg(val key: String, val type: NavType<*>) {
    CoinId("coinId", NavType.StringType),
}
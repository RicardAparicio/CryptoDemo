package com.ricardaparicio.cryptodemo.core.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class NavRoute(
    private val route: String,
    private val navArgs: List<NavArg> = emptyList(),
) {

    object CoinDetail : NavRoute("coinDetail", listOf(NavArg.CoinId))

    val args: List<NamedNavArgument> = navArgs.map { navArg ->
        navArgument(navArg.key) {
            type = navArg.type
        }
    }

    val destination: String = run {
        val argKeys = navArgs.map { navArg -> "{${navArg.key}}" }
        buildList {
            add(route)
            addAll(argKeys)
        }.joinToString("/")
    }
}

enum class NavArg(val key: String, val type: NavType<*>) {
    CoinId("coinId", NavType.StringType),
}
package com.ricardaparicio.cryptodemo.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ricardaparicio.cryptodemo.core.navigation.NavRoute
import com.ricardaparicio.cryptodemo.features.list.ui.CoinListScreen

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
            CoinListScreen()
        }
        composable(
            route = NavRoute.CoinDetail.destination,
            arguments = NavRoute.CoinDetail.args,
        ) {

        }
    }
}
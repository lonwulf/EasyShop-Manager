package com.lonwulf.labs.easyshopmanager.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

interface NavComposable {
    @Composable
    fun Composable(navHostController: NavHostController)
}

@Composable
fun NavigationGraph(
    navHostController: NavHostController,
    composable: Map<String, NavComposable>,
    startDestination: String,
) {
//    val destination = TopLevelDestinations.HomeScreen.route
    NavHost(navController = navHostController, startDestination = startDestination) {
        composable.forEach { (route, composable) ->
            composable(route = route) { backStackEntry ->
                composable.Composable(
                    navHostController = navHostController,
                )
            }
        }
    }

}
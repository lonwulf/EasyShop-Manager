package com.lonwulf.labs.easyshopmanager.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backpack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

object DestinationsConstants {
    const val HOME_SCREEN = "Home"
    const val SETTINGS_SCREEN = "Settings"
    const val PRODUCTS_SCREEN = "Products"

    const val FORGOT_PASS_REQUEST_SCREEN = "_FORGOT_PASS_REQUEST_SCREEN"

}

sealed class Destinations(val title: String, val route:String) {
    object ForgotPasswordScreen :
        Destinations(DestinationsConstants.FORGOT_PASS_REQUEST_SCREEN, "Forgot Password")
}

sealed class TopLevelDestinations(val route: String, val icon: ImageVector, val title: String) {
    object HomeScreen :
        TopLevelDestinations(DestinationsConstants.HOME_SCREEN, Icons.Outlined.Home, "Home")

    object ProductsScreen : TopLevelDestinations(
        DestinationsConstants.PRODUCTS_SCREEN,
        Icons.Outlined.Backpack,
        "Products"
    )
    object SettingsScreen : TopLevelDestinations(
        DestinationsConstants.SETTINGS_SCREEN,
        Icons.Outlined.Settings,
        "Settings"
    )
}
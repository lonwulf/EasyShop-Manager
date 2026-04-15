package com.lonwulf.labs.easyshopmanager.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backpack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

object DestinationsConstants {
    const val HOME_SCREEN = "Easy Shop"
    const val SETTINGS_SCREEN = "Settings"
    const val PRODUCTS_SCREEN = "Products"

    const val FORGOT_PASS_REQUEST_SCREEN = "_FORGOT_PASS_REQUEST_SCREEN"
    const val SCANNER_SCREEN = "_SCANNER_SCREEN"

}

sealed class Destinations(val title: String, val route:String) {
    object ForgotPasswordScreen :
        Destinations(DestinationsConstants.FORGOT_PASS_REQUEST_SCREEN, "Forgot Password")
    object ScannerScreen :
        Destinations(DestinationsConstants.SCANNER_SCREEN, "Scanner Screen")
}

sealed class TopLevelDestinations(val route: String, val icon: ImageVector, val title: String) {
    object HomeScreen :
        TopLevelDestinations(DestinationsConstants.HOME_SCREEN, Icons.Outlined.Home, "Easy Shop")

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
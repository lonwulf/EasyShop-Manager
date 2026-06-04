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
    const val OBJECT_DETECTION_SCREEN = "_OBJECT_DETECTION_SCREEN"
    const val MANUAL_INPUT_SCREEN = "CREATE PRODUCT"
    const val SIGN_UP_SCREEN = "SIGN UP"
    const val SIGN_IN_SCREEN = "SIGN IN"

}

sealed class Destinations(val route: String, val title: String) {
    object ResetPasswordScreen :
        Destinations(DestinationsConstants.FORGOT_PASS_REQUEST_SCREEN, "Reset Password")

    object ScannerScreen :
        Destinations(DestinationsConstants.SCANNER_SCREEN, "Scanner Screen")

    object ObjectDetectionScreen :
        Destinations(DestinationsConstants.OBJECT_DETECTION_SCREEN, "Object detection Screen")

    object ManualInputScreen :
        Destinations(DestinationsConstants.MANUAL_INPUT_SCREEN, "Create Product")

    object SignUpScreen: Destinations(DestinationsConstants.SIGN_UP_SCREEN, "Sign Up")
    object SignInScreen: Destinations(DestinationsConstants.SIGN_IN_SCREEN, "Sign In")
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
package com.lonwulf.labs.easyshopmanager.ui.screens

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable

class ManualInputScreenComposable : NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        snackbarHostState: SnackbarHostState
    ) {
        ManualInputScreen()
    }
}

@Composable
fun ManualInputScreen(modifier: Modifier = Modifier) {

}
package com.lonwulf.labs.easyshopmanager.presentation.screens

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable
import com.lonwulf.labs.easyshopmanager.presentation.viewmodel.MainViewModel

class SettingsScreenComposable(private val mainViewModel: MainViewModel) : NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        snackbarHostState: SnackbarHostState
    ) {
        SettingsScreen(navHostController = navHostController, mainViewModel = mainViewModel)
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, navHostController: NavHostController, mainViewModel: MainViewModel) {
}
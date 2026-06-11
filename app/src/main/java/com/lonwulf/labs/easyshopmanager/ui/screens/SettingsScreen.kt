package com.lonwulf.labs.easyshopmanager.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.MainViewModel

class SettingsScreenComposable(private val mainViewModel: MainViewModel) : NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController,
    ) {
        SettingsScreen(navHostController = navHostController, mainViewModel = mainViewModel)
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, navHostController: NavHostController, mainViewModel: MainViewModel) {
}
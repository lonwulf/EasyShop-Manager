package com.lonwulf.labs.easyshopmanager.presentation.screens

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable
import com.lonwulf.labs.easyshopmanager.presentation.viewmodel.MainViewModel

class HomeScreenComposable(private val mainViewModel: MainViewModel) : NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        snackbarHostState: SnackbarHostState
    ) {
        HomeScreen(mainViewModel = mainViewModel, navHostController = navHostController)
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier, mainViewModel: MainViewModel, navHostController: NavHostController) {

}
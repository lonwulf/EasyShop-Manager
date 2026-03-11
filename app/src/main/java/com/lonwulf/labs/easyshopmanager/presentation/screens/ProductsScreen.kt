package com.lonwulf.labs.easyshopmanager.presentation.screens

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable
import com.lonwulf.labs.easyshopmanager.presentation.viewmodel.MainViewModel

class ProductsScreenComposable(private val mainViewModel: MainViewModel): NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        snackbarHostState: SnackbarHostState
    ) {
        ProductsScreen(navHostController = navHostController, mainViewModel = mainViewModel)
    }
}

@Composable
fun ProductsScreen(modifier: Modifier = Modifier, navHostController: NavHostController, mainViewModel: MainViewModel) {

}
package com.lonwulf.labs.easyshopmanager.ui.screens

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

class ProductsScreenComposable(private val mainViewModel: MainViewModel) : NavComposable {
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

@Composable
@Preview(showBackground = true)
fun ProductsScreenPreview() {
    ProductsScreen(navHostController = NavHostController(LocalContext.current), mainViewModel = koinViewModel())
}
package com.lonwulf.labs.easyshopmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.lonwulf.labs.easyshopmanager.navigation.Destinations
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable
import com.lonwulf.labs.easyshopmanager.ui.components.AppLoaderComponent
import com.lonwulf.labs.easyshopmanager.ui.components.EmptyViewComponent
import com.lonwulf.labs.easyshopmanager.ui.components.ProductListComponent
import com.lonwulf.labs.easyshopmanager.ui.components.SearchFieldComponent
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

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
    val state by mainViewModel.productsState.collectAsStateWithLifecycle()
    var searchString by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        mainViewModel.fetchCachedProducts()
    }
    when {
        state.isLoading -> AppLoaderComponent()
        state.error != null -> {}
        else -> {
            if (state.products.isEmpty()) {
                EmptyViewComponent(onclick = {
                    navHostController.navigate(Destinations.ManualInputScreen.route)
//                    navHostController.navigate(Destinations.ScannerScreen.route)
                })
            } else {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(horizontal = 5.dp, vertical = 2.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    SearchFieldComponent(
                        value = searchString,
                        onValueChange = { searchString = it },
                        onSearchClick = {}
                    )

                    ProductListComponent(productList = state.products)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen(navHostController = NavHostController(LocalContext.current), mainViewModel = koinViewModel())

}
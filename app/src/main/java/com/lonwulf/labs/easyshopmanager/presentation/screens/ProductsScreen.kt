package com.lonwulf.labs.easyshopmanager.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
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
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable
import com.lonwulf.labs.easyshopmanager.presentation.components.EmptyViewComponent
import com.lonwulf.labs.easyshopmanager.presentation.components.ProgressLoader
import com.lonwulf.labs.easyshopmanager.presentation.components.SearchFieldComponent
import com.lonwulf.labs.easyshopmanager.presentation.viewmodel.MainViewModel
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
    val state by mainViewModel.productsState.collectAsStateWithLifecycle()
    var searchString by remember { mutableStateOf("") }
    when {
        state.isLoading -> ProgressLoader()
        state.error != null -> {}
        else -> {
            if (state.products.isEmpty()) {
                EmptyViewComponent(onclick = {})
            } else {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(horizontal = 5.dp, vertical = 2.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    SearchFieldComponent(
                        value = searchString,
                        onValueChange = {searchString = it},
                        onSearchClick = {}
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ProductsScreenPreview() {
    ProductsScreen(navHostController = NavHostController(LocalContext.current), mainViewModel = koinViewModel())
}
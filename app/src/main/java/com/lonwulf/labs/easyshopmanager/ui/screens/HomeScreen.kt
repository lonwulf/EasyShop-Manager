package com.lonwulf.labs.easyshopmanager.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.lonwulf.labs.easyshopmanager.ui.components.CategoriesListComponent
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
    val productsState by mainViewModel.productsState.collectAsStateWithLifecycle()
    val categoriesState by mainViewModel.categoriesState.collectAsStateWithLifecycle()
    val categories = remember(categoriesState) {
        categoriesState.categories.take(7)
    }
    var searchString by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        mainViewModel.fetchCachedProducts()
        mainViewModel.fetchCachedCategories()
    }
    when {
        productsState.isLoading -> AppLoaderComponent()
        productsState.error != null -> {}
        else -> {
            if (productsState.products.isEmpty()) {
                EmptyViewComponent(onclick = {
                    navHostController.navigate(Destinations.ManualInputScreen.route)
//                    navHostController.navigate(Destinations.ScannerScreen.route)
                })
            } else {
                Column(
                    modifier = modifier
                        .fillMaxSize(),
                ) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                        exit = fadeOut() + slideOutVertically(
                            targetOffsetY = { it / 2 }
                        ),
                        label = "search_bar_animation"
                    ) {
                        Box(
                            Modifier
                                .wrapContentSize()
                                .background(color = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column {
                                SearchFieldComponent(
                                    value = searchString,
                                    onValueChange = { searchString = it },
                                    onSearchClick = {}
                                )
                                Spacer(Modifier.height(20.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Text("Categories", modifier = Modifier.padding(start = 10.dp))
                    CategoriesListComponent(modifier = Modifier.fillMaxWidth(), categoriesList = categories)

                    Spacer(Modifier.height(10.dp))

                    Text("Products", modifier = Modifier.padding(start = 10.dp))
                    ProductListComponent(productList = productsState.products)
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
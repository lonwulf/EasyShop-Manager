package com.lonwulf.labs.easyshopmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lonwulf.labs.easyshopmanager.navigation.Destinations
import com.lonwulf.labs.easyshopmanager.navigation.NavigationGraph
import com.lonwulf.labs.easyshopmanager.navigation.TopLevelDestinations
import com.lonwulf.labs.easyshopmanager.ui.components.CustomFabComponent
import com.lonwulf.labs.easyshopmanager.ui.screens.HomeScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.screens.LiveBarcodeScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.screens.ObjectDetectionScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.screens.ProductsScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.screens.SettingsScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.theme.EasyShopManagerTheme
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyShopManagerTheme {
                val navHostController = rememberNavController()
                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val snackbarHostState = remember { SnackbarHostState() }
                val mainViewModel: MainViewModel =
                    koinViewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
                var fabActionState by remember { mutableStateOf(false) }

                val hideAppbarsInScreens = listOf(Destinations.ScannerScreen.route)
                val showAppbars = currentDestination?.route !in hideAppbarsInScreens
                val screensToShowFAB = listOf(
                    Destinations.ScannerScreen.route,
                    TopLevelDestinations.ProductsScreen.route,
                    TopLevelDestinations.HomeScreen.route
                )
                val showFAB = currentDestination?.route in screensToShowFAB
                Scaffold(
                    topBar = { if (showAppbars) AppToolBar(title = currentDestination?.route ?: "") },
                    bottomBar = { if (showAppbars) AppBottomBar(navHostController, currentDestination) },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    floatingActionButton = {
                        if (showFAB) {
                            CustomFabComponent(onclick = { fabActionState = true })
                            fabActionState = false
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        val composables = mapOf(
                            TopLevelDestinations.HomeScreen.route to HomeScreenComposable(
                                mainViewModel
                            ),
                            TopLevelDestinations.ProductsScreen.route to ProductsScreenComposable(
                                mainViewModel
                            ),
                            TopLevelDestinations.SettingsScreen.route to SettingsScreenComposable(
                                mainViewModel
                            ),
                            Destinations.ScannerScreen.route to LiveBarcodeScreenComposable(),
                            Destinations.ObjectDetectionScreen.route to ObjectDetectionScreenComposable()
                        )
                        NavigationGraph(
                            navHostController = navHostController,
                            snackbarHostState = snackbarHostState,
                            composable = composables
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AppToolBar(title: String) {
        TopAppBar(
            title = { Text(text = title) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                scrolledContainerColor = colorResource(
                    id = R.color.white
                ),
                navigationIconContentColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.background,
                actionIconContentColor = colorResource(
                    id = R.color.white
                )
            )
        )
    }

    @Composable
    private fun AppBottomBar(navHostController: NavHostController, currentDestination: NavDestination?) {
        val screens = listOf(
            TopLevelDestinations.HomeScreen,
            TopLevelDestinations.ProductsScreen,
            TopLevelDestinations.SettingsScreen
        )
        NavigationBar(
            containerColor = colorResource(R.color.purple_700),
            contentColor = colorResource(R.color.white),
            tonalElevation = 5.dp
        ) {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navHostController = navHostController
                )
            }
        }
    }

    @Composable
    private fun RowScope.AddItem(
        screen: TopLevelDestinations,
        currentDestination: NavDestination?,
        navHostController: NavHostController
    ) {
        NavigationBarItem(
            label = { Text(text = screen.title) },
            colors = NavigationBarItemColors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                selectedIndicatorColor = colorResource(R.color.purple_200),
                disabledIconColor = Color.Gray,
                disabledTextColor = Color.Gray
            ),
            selected = currentDestination?.route == screen.route,
            onClick = {
                navHostController.navigate(screen.route) {
                    popUpTo(navHostController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            },
            icon = {
                BadgedBox(badge = {}) {
                    Icon(imageVector = screen.icon, contentDescription = "bottom bar icon")
                }
            })
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EasyShopManagerTheme {
    }
}
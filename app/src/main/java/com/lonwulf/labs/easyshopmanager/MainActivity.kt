package com.lonwulf.labs.easyshopmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.lonwulf.labs.easyshopmanager.navigation.Destinations
import com.lonwulf.labs.easyshopmanager.navigation.NavigationGraph
import com.lonwulf.labs.easyshopmanager.navigation.TopLevelDestinations
import com.lonwulf.labs.easyshopmanager.ui.components.CustomFabComponent
import com.lonwulf.labs.easyshopmanager.ui.components.UpButtonComponent
import com.lonwulf.labs.easyshopmanager.ui.screens.HomeScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.screens.LiveBarcodeScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.screens.ManualInputScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.screens.ObjectDetectionScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.screens.ProductsScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.screens.SettingsScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.theme.EasyShopManagerTheme
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.MainViewModel
import com.lonwulf.labs.easyshopmanager.util.SyncEvent
import org.koin.androidx.compose.koinViewModel
import org.koin.mp.KoinPlatform.getKoin


class MainActivity : ComponentActivity() {
    private val syncEvent: SyncEvent = getKoin().get()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyShopManagerTheme(darkTheme = false) {
                val navHostController = rememberNavController()
                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val snackbarHostState = remember { SnackbarHostState() }
                val mainViewModel: MainViewModel =
                    koinViewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
                var fabActionState by remember { mutableStateOf(false) }

                val hideAppbarsInScreens = listOf(Destinations.ScannerScreen.route)
                val hideBottomBarsInScreens =
                    listOf(Destinations.ScannerScreen.route, Destinations.ManualInputScreen.route)
                val showAppbars = currentDestination?.route !in hideAppbarsInScreens
                val showBottomBars = currentDestination?.route !in hideBottomBarsInScreens
                val screensToShowFAB = listOf(
                    Destinations.ScannerScreen.route,
                    TopLevelDestinations.ProductsScreen.route,
                    TopLevelDestinations.HomeScreen.route
                )
                val showFAB = currentDestination?.route in screensToShowFAB
                Scaffold(
                    topBar = {
                        AnimatedVisibility(
                            visible = showAppbars,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically(
                                targetOffsetY = { it / 2 }
                            ),
                            label = "app_bar_animation"
                        ) {
                            AppToolbar(
                                title = currentDestination?.route ?: "",
                                syncEvent,
                                currentDestination,
                                navHostController
                            )
                        }
                    },
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomBars,
                            enter = fadeIn(animationSpec = tween(300)) + slideInVertically(),
                            exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                                targetOffsetY = { it / 2 }
                            ),
                            label = "bottom_bar_animation"
                        ) {
                            AppBottombar(navHostController, currentDestination)
                        }
                    },
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
                        color = MaterialTheme.colorScheme.surfaceContainer,
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
                            Destinations.ObjectDetectionScreen.route to ObjectDetectionScreenComposable(),
                            Destinations.ManualInputScreen.route to ManualInputScreenComposable()
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
    private fun AppToolbar(
        title: String,
        syncEvent: SyncEvent,
        currentDestination: NavDestination?,
        navHostController: NavHostController
    ) {
        val syncState by syncEvent.syncState.collectAsState()
        val isSyncing = syncState == SyncEvent.SyncState.SYNCING
        val screens = listOf(
            TopLevelDestinations.HomeScreen.route,
            TopLevelDestinations.ProductsScreen.route,
            TopLevelDestinations.SettingsScreen.route
        )
        val showUpButton = currentDestination?.route !in screens

        TopAppBar(
            title = {
                AnimatedContent(
                    targetState = title,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    label = "title_animation"
                ) { animatedTitle ->
                    Text(text = animatedTitle)
                }
            },
            navigationIcon = {
                AnimatedVisibility(
                    visible = showUpButton,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically(
                        targetOffsetY = { it / 2 }
                    ),
                    label = "up_button_label"
                ) {
                    UpButtonComponent {
                        navHostController.popBackStack()
                    }
                }
            },
            actions = {
                AnimatedVisibility(
                    visible = isSyncing,
                    enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it / 2 }),
                    exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { it / 2 })
                ) {
                    SyncIndicator()
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                scrolledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.background,
                actionIconContentColor = colorResource(
                    id = R.color.white
                )
            )
        )
    }

    @Composable
    private fun SyncIndicator() {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.download))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            speed = 1.2f,
            isPlaying = true
        )
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(30.dp)
        )
    }

    @Composable
    private fun AppBottombar(navHostController: NavHostController, currentDestination: NavDestination?) {
        val screens = listOf(
            TopLevelDestinations.HomeScreen,
            TopLevelDestinations.ProductsScreen,
            TopLevelDestinations.SettingsScreen
        )
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            tonalElevation = 8.dp
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
        val isSelected = currentDestination?.route == screen.route

        val iconScale by animateFloatAsState(
            targetValue = if (isSelected) 1.2f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "icon_scale"
        )
        val animatedIconColor by animateColorAsState(
            targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Gray,
            label = "icon_color"
        )
        val animatedIndicatorColor by animateColorAsState(
            targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Transparent,
            label = "indicator_color"
        )
        NavigationBarItem(
            selected = isSelected,
            label = {
                AnimatedVisibility(
                    visible = isSelected,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Text(text = screen.title)
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
//                unselectedIconColor = Color.Gray,
//                unselectedTextColor = Color.Gray,
                indicatorColor = animatedIndicatorColor,
            ),
            onClick = {
                navHostController.navigate(screen.route) {
                    popUpTo(navHostController.graph.findStartDestination().id)
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                BadgedBox(badge = {}) {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = "bottom bar icon",
                        tint = animatedIconColor,
                        modifier = Modifier.scale(iconScale)
                    )
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
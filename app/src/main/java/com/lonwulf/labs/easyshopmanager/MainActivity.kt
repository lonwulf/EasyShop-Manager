package com.lonwulf.labs.easyshopmanager

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.lonwulf.labs.easyshopmanager.auth.domain.state.AuthState
import com.lonwulf.labs.easyshopmanager.auth.ui.screens.SignInScreenComposable
import com.lonwulf.labs.easyshopmanager.auth.ui.screens.SignUpScreenComposable
import com.lonwulf.labs.easyshopmanager.core.util.SyncEvent
import com.lonwulf.labs.easyshopmanager.navigation.Destinations
import com.lonwulf.labs.easyshopmanager.navigation.NavigationGraph
import com.lonwulf.labs.easyshopmanager.navigation.TopLevelDestinations
import com.lonwulf.labs.easyshopmanager.presentation.ui.components.CustomFabComponent
import com.lonwulf.labs.easyshopmanager.presentation.ui.components.UpButtonComponent
import com.lonwulf.labs.easyshopmanager.presentation.ui.theme.EasyShopManagerTheme
import com.lonwulf.labs.easyshopmanager.ui.screens.HomeScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.screens.InventoryScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.screens.LiveBarcodeScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.screens.ManualInputScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.screens.ObjectDetectionScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.screens.SettingsScreenComposable
import com.lonwulf.labs.easyshopmanager.ui.viewmodel.MainViewModel
import com.lonwulf.labs.easyshopmanager.worker.SyncWorker
import org.koin.mp.KoinPlatform.getKoin


class MainActivity : ComponentActivity() {

    private val HIDE_TOP_BAR_ROUTES = setOf(
        Destinations.ScannerScreen.route,
        Destinations.SignInScreen.route,
        Destinations.SignUpScreen.route
    )

    private val HIDE_BOTTOM_BAR_ROUTES = setOf(
        Destinations.ScannerScreen.route,
        Destinations.ManualInputScreen.route,
        Destinations.SignUpScreen.route,
        Destinations.SignInScreen.route
    )

    private val SHOW_FAB_ROUTES = setOf(
        Destinations.ScannerScreen.route,
        TopLevelDestinations.InventoryScreen.route,
        TopLevelDestinations.HomeScreen.route
    )

    private val BOTTOM_NAV_SCREENS = setOf(
        TopLevelDestinations.HomeScreen,
        TopLevelDestinations.InventoryScreen,
        TopLevelDestinations.SettingsScreen
    )

    private val TOP_LEVEL_SCREEN_ROUTES = setOf(
        TopLevelDestinations.HomeScreen.route,
        TopLevelDestinations.InventoryScreen.route,
        TopLevelDestinations.SettingsScreen.route
    )
    private val syncEvent: SyncEvent = getKoin().get()

    override fun onStart() {
        super.onStart()
        setupSyncWorker(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val mainViewModel: MainViewModel = getKoin().get()
        splashScreen.setKeepOnScreenCondition {
            mainViewModel.authState.value is AuthState.Loading
        }
        setContent {
            EasyShopManagerTheme(darkTheme = false) {
                val navHostController = rememberNavController()
                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val snackbarHostState = remember { SnackbarHostState() }
//                val mainViewModel: MainViewModel =
//                    koinViewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
                var fabActionState by remember { mutableStateOf(false) }
                val currentRoute = currentDestination?.route

                val showAppbars by remember(currentRoute) {
                    derivedStateOf { currentRoute != null && currentRoute !in HIDE_TOP_BAR_ROUTES }
                }

                val showBottomBars by remember(currentRoute) {
                    derivedStateOf { currentRoute != null && currentRoute !in HIDE_BOTTOM_BAR_ROUTES }
                }

                val showFAB by remember(currentRoute) {
                    derivedStateOf { currentRoute != null && currentRoute in SHOW_FAB_ROUTES }
                }
                val authState by mainViewModel.authState.collectAsStateWithLifecycle()

                val composables = remember(mainViewModel) {//tied to the mainViewModel, so it's allocated exactly once
                    mapOf(
                        TopLevelDestinations.HomeScreen.route to HomeScreenComposable(mainViewModel),
                        TopLevelDestinations.InventoryScreen.route to InventoryScreenComposable(mainViewModel),
                        TopLevelDestinations.SettingsScreen.route to SettingsScreenComposable(mainViewModel),
                        Destinations.ScannerScreen.route to LiveBarcodeScreenComposable(),
                        Destinations.ObjectDetectionScreen.route to ObjectDetectionScreenComposable(),
                        Destinations.ManualInputScreen.route to ManualInputScreenComposable(),
                        Destinations.SignUpScreen.route to SignUpScreenComposable(),
                        Destinations.SignInScreen.route to SignInScreenComposable(),
                    )
                }
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
                                title = currentRoute ?: "",
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
                        when (authState) {
                            is AuthState.Loading -> {
                                // This box acts as a secondary fallback, but
                                // the user won't see it because the native splash
                                // screen covers this state completely
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }

                            is AuthState.Unauthenticated -> NavigationGraph(
                                navHostController = navHostController,
                                composable = composables,
                                startDestination = Destinations.SignInScreen.route
                            )

                            is AuthState.Authenticated -> NavigationGraph(
                                navHostController = navHostController,
                                composable = composables,
                                startDestination = TopLevelDestinations.HomeScreen.route
                            )
                        }
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
        val syncState by syncEvent.syncState.collectAsStateWithLifecycle()
        val isSyncing = syncState == SyncEvent.SyncState.SYNCING
        val showUpButton by remember(currentDestination) {
            derivedStateOf {
                val currentRoute = currentDestination?.route
                // Show up button only if we have a valid route and it's NOT a top-level screen
                currentRoute != null && currentRoute !in TOP_LEVEL_SCREEN_ROUTES
            }
        }

        TopAppBar(
            title = {
                AnimatedContent(
                    targetState = title,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    label = "title_animation"
                ) { animatedTitle ->
                    Text(
                        text = animatedTitle,
                        style = MaterialTheme.typography.titleLarge
                    )
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
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(com.lonwulf.labs.easyshopmanager.presentation.R.raw.download))
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
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            tonalElevation = 8.dp
        ) {
            BOTTOM_NAV_SCREENS.forEach { screen ->
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
        val isSelected by remember(currentDestination) {
            derivedStateOf {
                currentDestination?.hierarchy?.any { it.route == screen.route } == true
            }
        }

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
                unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = animatedIndicatorColor,
            ),
            onClick = {
                if (!isSelected) {
                    navHostController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to avoid building up a huge stack of destinations
                        popUpTo(navHostController.graph.findStartDestination().id) {
                            saveState = true // CRITICAL: Save state when popping off the stack
                        }
                        // Avoid multiple copies of the same destination when reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            },
            icon = {
                BadgedBox(badge = {}) {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = "bottom bar icon",
                        tint = animatedIconColor,
                        modifier = Modifier.graphicsLayer {
                            scaleX = iconScale
                            scaleY = iconScale
                        }
                    )
                }
            })
    }

    private fun setupSyncWorker(context: Context) {
        Log.e("MainAct: ", "setupSyncWorker")

        val syncWorker = OneTimeWorkRequestBuilder<SyncWorker>().build()
        WorkManager.getInstance(context)
            .beginUniqueWork("full_sync", ExistingWorkPolicy.REPLACE, syncWorker)
            .enqueue()
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    EasyShopManagerTheme {
    }
}
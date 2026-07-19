package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: TruBiteViewModel = viewModel()
                
                // Track backstack to conditionally show Bottom Navigation
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val mainTabRoutes = listOf(
                    Destination.HOME,
                    Destination.SCANNER,
                    Destination.SEARCH,
                    Destination.CHAT,
                    Destination.FAMILY_DASHBOARD
                )

                val showBottomBar = currentRoute in mainTabRoutes

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.testTag("bottom_nav_bar")
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute == Destination.HOME,
                                    onClick = {
                                        navController.navigate(Destination.HOME) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text("Home") },
                                    modifier = Modifier.testTag("nav_tab_home")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == Destination.SCANNER,
                                    onClick = {
                                        navController.navigate(Destination.SCANNER) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Scanner") },
                                    label = { Text("Scanner") },
                                    modifier = Modifier.testTag("nav_tab_scanner")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == Destination.SEARCH,
                                    onClick = {
                                        navController.navigate(Destination.SEARCH) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                                    label = { Text("Search") },
                                    modifier = Modifier.testTag("nav_tab_search")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == Destination.CHAT,
                                    onClick = {
                                        navController.navigate(Destination.CHAT) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(imageVector = Icons.Default.Chat, contentDescription = "Chat") },
                                    label = { Text("AI Twin") },
                                    modifier = Modifier.testTag("nav_tab_chat")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == Destination.FAMILY_DASHBOARD,
                                    onClick = {
                                        navController.navigate(Destination.FAMILY_DASHBOARD) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(imageVector = Icons.Default.Group, contentDescription = "Family") },
                                    label = { Text("Family") },
                                    modifier = Modifier.testTag("nav_tab_family")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Destination.SPLASH,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 1. Splash Screen
                        composable(Destination.SPLASH) {
                            SplashScreen(
                                onNavigateToOnboarding = {
                                    navController.navigate(Destination.ONBOARDING) {
                                        popUpTo(Destination.SPLASH) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 2. Onboarding Screen
                        composable(Destination.ONBOARDING) {
                            OnboardingScreen(
                                onNavigateToLogin = {
                                    navController.navigate(Destination.LOGIN) {
                                        popUpTo(Destination.ONBOARDING) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 3. Login Screen
                        composable(Destination.LOGIN) {
                            LoginScreen(
                                onNavigateToProfileCreation = {
                                    navController.navigate(Destination.PROFILE_CREATION) {
                                        popUpTo(Destination.LOGIN) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 4. Health Profile Creation Screen
                        composable(Destination.PROFILE_CREATION) {
                            ProfileCreationScreen(
                                viewModel = viewModel,
                                onNavigateToHome = {
                                    navController.navigate(Destination.HOME) {
                                        popUpTo(Destination.PROFILE_CREATION) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 5. Home Dashboard Screen
                        composable(Destination.HOME) {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToProduct = { barcode ->
                                    navController.navigate(Destination.PRODUCT_DETAILS)
                                },
                                onNavigateToScanner = {
                                    navController.navigate(Destination.SCANNER)
                                },
                                onNavigateToSearch = {
                                    navController.navigate(Destination.SEARCH)
                                },
                                onNavigateToChat = {
                                    navController.navigate(Destination.CHAT)
                                },
                                onNavigateToCartOptimizer = {
                                    navController.navigate(Destination.CART_OPTIMIZER)
                                }
                            )
                        }

                        // 6. Barcode Scanner Screen
                        composable(Destination.SCANNER) {
                            ScannerScreen(
                                viewModel = viewModel,
                                onNavigateToProduct = { barcode ->
                                    navController.navigate(Destination.PRODUCT_DETAILS)
                                }
                            )
                        }

                        // 7. Product Search Screen
                        composable(Destination.SEARCH) {
                            SearchScreen(
                                viewModel = viewModel,
                                onNavigateToProduct = { barcode ->
                                    navController.navigate(Destination.PRODUCT_DETAILS)
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 8. AI Chat Assistant Screen
                        composable(Destination.CHAT) {
                            ChatScreen(viewModel = viewModel)
                        }

                        // 9. Family Dashboard Screen
                        composable(Destination.FAMILY_DASHBOARD) {
                            FamilyDashboardScreen(
                                viewModel = viewModel,
                                onNavigateToCreateProfile = {
                                    navController.navigate(Destination.PROFILE_CREATION)
                                }
                            )
                        }

                        // 10. Product Details Screen
                        composable(Destination.PRODUCT_DETAILS) {
                            ProductDetailsScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 11. Cart Optimizer Screen
                        composable(Destination.CART_OPTIMIZER) {
                            CartOptimizerScreen(
                                viewModel = viewModel,
                                onNavigateToProduct = { barcode ->
                                    navController.navigate(Destination.PRODUCT_DETAILS)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

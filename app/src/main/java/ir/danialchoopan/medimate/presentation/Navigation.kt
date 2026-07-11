package ir.danialchoopan.medimate.presentation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import ir.danialchoopan.medimate.presentation.screens.*
import ir.danialchoopan.medimate.presentation.viewmodel.*

sealed class Screen(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Dashboard : Screen("dashboard", "خانه", Icons.Filled.Home, Icons.Filled.Home)
    data object AddMedicine : Screen("add_medicine", "افزودن", Icons.Filled.Add, Icons.Filled.Add)
    data object History : Screen("history", "تاریخچه", Icons.Filled.List, Icons.Filled.List)
    data object Settings : Screen("settings", "تنظیمات", Icons.Filled.Settings, Icons.Filled.Settings)
    data object EditMedicine : Screen("edit_medicine/{medicineId}", "ویرایش", Icons.Filled.Home, Icons.Filled.Home)
}

private val bottomBarScreens = listOf(Screen.Dashboard, Screen.AddMedicine, Screen.History, Screen.Settings)

@Composable
fun MedicineReminderApp(
    isDarkMode: Boolean = false
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in bottomBarScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomBarScreens.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.label
                                )
                            },
                            label = { Text(screen.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(padding),
            enterTransition = {
                fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(300)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(300)
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(300)
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(300)
                )
            }
        ) {
            composable(Screen.Dashboard.route) {
                val viewModel: DashboardViewModel = hiltViewModel()
                DashboardScreen(viewModel = viewModel, navController = navController)
            }
            composable(Screen.AddMedicine.route) {
                val viewModel: AddMedicineViewModel = hiltViewModel()
                AddMedicineScreen(viewModel = viewModel, navController = navController)
            }
            composable(Screen.History.route) {
                val viewModel: HistoryViewModel = hiltViewModel()
                HistoryScreen(viewModel = viewModel)
            }
            composable(Screen.Settings.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(viewModel = viewModel, navController = navController)
            }
            composable(Screen.EditMedicine.route) { backStackEntry ->
                val medicineId = backStackEntry.arguments?.getInt("medicineId") ?: return@composable
                val viewModel: AddMedicineViewModel = hiltViewModel()
                EditMedicineScreen(medicineId = medicineId, viewModel = viewModel, navController = navController)
            }
            composable("about") {
                AboutScreen(navController = navController)
            }
            composable("inventory") {
                val viewModel: InventoryViewModel = hiltViewModel()
                InventoryScreen(viewModel = viewModel, navController = navController)
            }
            composable("onboarding") {
                OnboardingScreen(onFinish = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo("onboarding") { inclusive = true }
                    }
                })
            }
        }
    }
}

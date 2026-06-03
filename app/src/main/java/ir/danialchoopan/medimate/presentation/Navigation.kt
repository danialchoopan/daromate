package ir.danialchoopan.medimate.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import ir.danialchoopan.medimate.presentation.screens.*
import ir.danialchoopan.medimate.presentation.viewmodel.*

@Composable
fun MedicineReminderApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            val viewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(viewModel = viewModel)
        }
        composable("add_medicine") {
            val viewModel: AddMedicineViewModel = hiltViewModel()
            AddMedicineScreen(viewModel = viewModel, navController = navController)
        }
        composable("history") {
            val viewModel: HistoryViewModel = hiltViewModel()
            HistoryScreen(viewModel = viewModel)
        }
    }
}

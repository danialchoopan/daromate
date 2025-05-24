package ir.nimaali.medimate.ui

// Navigation.kt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.nimaali.medimate.data.AppDatabase
import ir.nimaali.medimate.ui.screens.AboutScreen
import ir.nimaali.medimate.ui.screens.AddMedicineScreen
import ir.nimaali.medimate.ui.screens.AddReminderForMedicineScreen
import ir.nimaali.medimate.ui.screens.MedicineDetailScreen
import ir.nimaali.medimate.ui.screens.MedicineListScreen
import ir.nimaali.medimate.ui.screens.SplashScreen
import ir.nimaali.medimate.viewmodel.MedicineViewModel

@Composable
fun MedicineReminderApp() {

    val navController = rememberNavController()
    val m_context = LocalContext.current
    val database = remember { AppDatabase.getInstance(m_context) }
    val viewModel = remember {
        MedicineViewModel(
            medicineDao = database.medicineDao(),
            reminderDao = database.reminderDao(),
            m_context
        )
    }

    NavHost(
        navController = navController,
        startDestination = "splash_medicine"
    ) {
        composable("splash_medicine") {
            SplashScreen(
                navController = navController
            )
        }

        composable("medicineList") {
            MedicineListScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable("addMedicine") {
            AddMedicineScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable("addReminder/{medicineId}") { backStackEntry ->
            val medicineId = backStackEntry.arguments?.getString("medicineId")?.toIntOrNull() ?: 0
            AddReminderForMedicineScreen(
                medicineId = medicineId,
                medicineDao = database.medicineDao(),
                reminderDao = database.reminderDao(),
                viewModel = viewModel,
                navController = navController
            )
        }
        composable("medicineDetail/{medicineId}") { backStackEntry ->
            val medicineId = backStackEntry.arguments?.getString("medicineId")?.toIntOrNull() ?: 0
            MedicineDetailScreen(
                viewModel = viewModel,
                medicineId = medicineId,
                medicineDao = database.medicineDao(),
                reminderDao = database.reminderDao(),
                navController = navController
            )
        }
        composable("about") {
            AboutScreen(navController)
        }
    }
}
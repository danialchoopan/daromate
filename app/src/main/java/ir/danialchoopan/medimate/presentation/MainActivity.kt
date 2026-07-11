package ir.danialchoopan.medimate.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import ir.danialchoopan.medimate.presentation.viewmodel.SettingsViewModel
import ir.danialchoopan.medimate.presentation.viewmodel.dataStore
import ir.danialchoopan.medimate.presentation.theme.MedicineReminderTheme
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()
        setContent {
            val context = LocalContext.current
            val isDarkMode by remember {
                context.dataStore.data.map { preferences ->
                    preferences[SettingsViewModel.DARK_MODE_KEY] ?: false
                }
            }.collectAsState(initial = false)

            MedicineReminderTheme(darkTheme = isDarkMode) {
                MedicineReminderApp()
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

package ir.danialchoopan.medimate.presentation

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import ir.danialchoopan.medimate.presentation.viewmodel.SettingsViewModel
import ir.danialchoopan.medimate.presentation.viewmodel.dataStore
import ir.danialchoopan.medimate.presentation.theme.MedicineReminderTheme
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var showPermissionDialog = mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            checkExactAlarmPermission()
        }
    }

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

                // Permission rationale dialog
                if (showPermissionDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showPermissionDialog.value = false },
                        title = { Text("دسترسی اعلان لازم است") },
                        text = { Text("برای دریافت یادآوری دارو، لطفاً دسترسی اعلان را فعال کنید.") },
                        confirmButton = {
                            Button(onClick = {
                                showPermissionDialog.value = false
                                openAppSettings()
                            }) {
                                Text("رفتن به تنظیمات")
                            }
                        }
                    )
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED -> {
                    // Already granted, check exact alarm
                    checkExactAlarmPermission()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show rationale
                    showPermissionDialog.value = true
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                try {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    // Fallback - app will use inexact alarms
                }
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        startActivity(intent)
    }
}

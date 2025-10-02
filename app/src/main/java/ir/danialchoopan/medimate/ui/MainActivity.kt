package ir.nimaali.medimate.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ir.nimaali.medimate.ui.theme.MyApplicationTheme

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ir.nimaali.medimate.util.NotificationUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // ساخت کانال نوتیفیکشن
        NotificationUtils.createNotificationChannel(this)

        setContent {
            RightToLeftLayout {
                MedicineReminderTheme {
                    val systemUiController = rememberSystemUiController()
                    val useDarkIcons = !isSystemInDarkTheme()

                    DisposableEffect(systemUiController, useDarkIcons) {
                        systemUiController.setSystemBarsColor(
                            color = Color.Transparent,
                            darkIcons = useDarkIcons
                        )
                        onDispose {}
                    }

                    MedicineReminderApp()
                }
            }
        }
    }
}

@Composable
fun MedicineReminderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF4CAF50), // سبز اصلی
            primaryContainer = Color(0xFFA5D6A7), // سبز روشن
            secondary = Color(0xFF81C784), // سبز متوسط
            secondaryContainer = Color(0xFFC8E6C9), // سبز بسیار روشن
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE8F5E9), // سبز خیلی کم رنگ
            onPrimary = Color(0xFFFFFFFF),
            onSecondary = Color(0xFFFFFFFF),
            onSurface = Color(0xFF000000),
        ),
        content = content
    )
}

@Composable
fun RightToLeftLayout(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        content()
    }
}

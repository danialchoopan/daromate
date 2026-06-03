package ir.danialchoopan.medimate.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ir.danialchoopan.medimate.util.NotificationUtils

@HiltAndroidApp
class MedicineApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationUtils.createNotificationChannel(this)
    }
}

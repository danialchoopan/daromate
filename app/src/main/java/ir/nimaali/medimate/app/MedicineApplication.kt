package ir.nimaali.medimate.app

import android.app.Application
import ir.nimaali.medimate.data.AppDatabase

class MedicineApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }
}
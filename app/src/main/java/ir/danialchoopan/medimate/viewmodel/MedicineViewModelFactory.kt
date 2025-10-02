package ir.nimaali.medimate.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ir.nimaali.medimate.data.dao.MedicineDao
import ir.nimaali.medimate.data.dao.ReminderDao

class MedicineViewModelFactory(
    private val medicineDao: MedicineDao,
    private val reminderDao: ReminderDao,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicineViewModel::class.java)) {
            return MedicineViewModel(medicineDao, reminderDao, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
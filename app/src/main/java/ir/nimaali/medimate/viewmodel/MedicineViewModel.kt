package ir.nimaali.medimate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.nimaali.medimate.data.dao.MedicineDao
import ir.nimaali.medimate.data.dao.ReminderDao
import ir.nimaali.medimate.data.table.IntervalType
import ir.nimaali.medimate.data.table.Medicine
import ir.nimaali.medimate.data.table.Reminder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicineViewModel(
    private val medicineDao: MedicineDao,
    private val reminderDao: ReminderDao
) : ViewModel() {

    private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicines: StateFlow<List<Medicine>> = _medicines.asStateFlow()

    init {
        viewModelScope.launch {
            medicineDao.getAllMedicines().collect { medicines ->
                _medicines.value = medicines
            }
        }
    }

    fun addMedicine(medicine: Medicine, reminderInterval: Pair<IntervalType, Int>) {
        viewModelScope.launch {
            val medicineId = medicineDao.insert(medicine).toInt()

            val nextReminderTime = calculateNextReminderTime(
                medicine.startDate,
                reminderInterval.first,
                reminderInterval.second
            )

            val reminder = Reminder(
                medicineId = medicineId,
                intervalType = reminderInterval.first,
                intervalValue = reminderInterval.second,
                nextReminderTime = nextReminderTime
            )

            reminderDao.insert(reminder)
        }
    }

    private fun calculateNextReminderTime(
        startTime: Long,
        intervalType: IntervalType,
        intervalValue: Int
    ): Long {
        return when (intervalType) {
            IntervalType.MINUTES -> startTime + intervalValue * 60 * 1000
            IntervalType.HOURS -> startTime + intervalValue * 60 * 60 * 1000
            IntervalType.DAYS -> startTime + intervalValue * 24 * 60 * 60 * 1000
            IntervalType.WEEKS -> startTime + intervalValue * 7 * 24 * 60 * 60 * 1000
        }
    }
}
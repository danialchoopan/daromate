package ir.danialchoopan.medimate.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import ir.danialchoopan.medimate.domain.repository.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimezoneChangeReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderRepository: ReminderRepository

    @Inject
    lateinit var medicineRepository: MedicineRepository

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val activeReminders = reminderRepository.getAllActiveReminders().first()
                activeReminders.forEach { reminder ->
                    val medicine = medicineRepository.getMedicineById(reminder.medicineId) ?: return@forEach
                    ReminderScheduler.scheduleReminder(context, reminder, medicine.name)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

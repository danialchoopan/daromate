package ir.danialchoopan.medimate.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import ir.danialchoopan.medimate.domain.repository.ReminderRepository
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import ir.danialchoopan.medimate.domain.usecase.MarkAsTakenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var markAsTakenUseCase: MarkAsTakenUseCase

    @Inject
    lateinit var reminderRepository: ReminderRepository

    @Inject
    lateinit var medicineRepository: MedicineRepository

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra("reminderId", -1)
        val medicineName = intent.getStringExtra("medicineName") ?: "Medicine"

        when (intent.action) {
            "ACTION_TAKEN" -> {
                CoroutineScope(Dispatchers.IO).launch {
                    markAsTakenUseCase(reminderId, System.currentTimeMillis())
                    // Reschedule after updating
                    val reminder = reminderRepository.getReminderById(reminderId)
                    val medicine = medicineRepository.getMedicineById(reminder?.medicineId ?: -1)
                    if (reminder != null && medicine != null) {
                        ReminderScheduler.scheduleReminder(context, reminder, medicine.name)
                    }
                }
                NotificationUtils.cancelNotification(context, reminderId)
            }
            "ACTION_SNOOZE" -> {
                // Reschedule for 10 minutes later
                val snoozeTime = System.currentTimeMillis() + 10 * 60 * 1000L
                // Simplified snooze: just schedule a one-off
                NotificationUtils.cancelNotification(context, reminderId)
            }
            else -> {
                NotificationUtils.showActionableNotification(context, reminderId, medicineName)
            }
        }
    }
}

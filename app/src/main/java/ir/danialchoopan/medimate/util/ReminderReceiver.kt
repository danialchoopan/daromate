package ir.danialchoopan.medimate.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import ir.danialchoopan.medimate.domain.model.IntervalType
import ir.danialchoopan.medimate.domain.model.Reminder
import ir.danialchoopan.medimate.domain.repository.ReminderRepository
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import ir.danialchoopan.medimate.domain.usecase.MarkAsTakenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (intent.action) {
                    "ACTION_TAKEN" -> handleTaken(context, intent)
                    "ACTION_SNOOZE" -> handleSnooze(context, intent)
                    "ACTION_DISMISS" -> handleDismiss(context, intent)
                    Intent.ACTION_BOOT_COMPLETED,
                    Intent.ACTION_MY_PACKAGE_REPLACED,
                    Intent.ACTION_TIMEZONE_CHANGED,
                    Intent.ACTION_TIME_CHANGED -> handleBootCompleted(context)
                    else -> {
                        val reminderId = intent.getIntExtra("reminderId", -1)
                        val medicineName = intent.getStringExtra("medicineName") ?: "دارو"
                        if (reminderId != -1) {
                            // Get medicine details for better notification
                            val reminder = reminderRepository.getReminderById(reminderId)
                            val medicine = reminder?.let { medicineRepository.getMedicineById(it.medicineId) }

                            NotificationUtils.showMedicineReminder(
                                context = context,
                                reminderId = reminderId,
                                medicineName = medicineName,
                                dosage = medicine?.dosage ?: "",
                                instruction = medicine?.instruction ?: ""
                            )
                            NotificationUtils.vibrateDevice(context)
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun handleTaken(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra("reminderId", -1)
        if (reminderId == -1) return

        markAsTakenUseCase(reminderId, System.currentTimeMillis())

        val reminder = reminderRepository.getReminderById(reminderId) ?: return
        val medicine = medicineRepository.getMedicineById(reminder.medicineId) ?: return

        NotificationUtils.cancelNotification(context, reminderId)
        ReminderScheduler.scheduleReminder(context, reminder, medicine.name)
    }

    private suspend fun handleSnooze(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra("reminderId", -1)
        val medicineName = intent.getStringExtra("medicineName") ?: "دارو"
        if (reminderId == -1) return

        NotificationUtils.cancelNotification(context, reminderId)

        // Snooze for 10 minutes
        val snoozeTime = System.currentTimeMillis() + 10 * 60 * 1000L
        val snoozeReminder = Reminder(
            id = reminderId,
            medicineId = 0,
            intervalType = IntervalType.MINUTES,
            intervalValue = 10,
            nextReminderTime = snoozeTime
        )
        ReminderScheduler.scheduleReminder(context, snoozeReminder, medicineName)
    }

    private suspend fun handleDismiss(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra("reminderId", -1)
        if (reminderId == -1) return
        NotificationUtils.cancelNotification(context, reminderId)
    }

    private suspend fun handleBootCompleted(context: Context) {
        // Wait a bit for system to fully boot
        kotlinx.coroutines.delay(5000)

        val activeReminders = reminderRepository.getAllActiveReminders().first()
        activeReminders.forEach { reminder ->
            val medicine = medicineRepository.getMedicineById(reminder.medicineId) ?: return@forEach
            if (reminder.nextReminderTime > System.currentTimeMillis()) {
                ReminderScheduler.scheduleReminder(context, reminder, medicine.name)
            }
        }
    }
}

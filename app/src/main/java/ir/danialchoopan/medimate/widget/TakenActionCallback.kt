package ir.danialchoopan.medimate.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import ir.danialchoopan.medimate.data.local.AppDatabase
import ir.danialchoopan.medimate.domain.model.LogStatus
import ir.danialchoopan.medimate.util.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class TakenActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: androidx.glance.action.ActionParameters
    ) {
        val reminderId = parameters[reminderIdParam] ?: return

        withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)

            // Get the reminder
            val reminderEntity = db.reminderDao().getReminderById(reminderId) ?: return@withContext

            // Log the taken event
            val log = ir.danialchoopan.medimate.data.local.entities.MedicationLogEntity(
                medicineId = reminderEntity.medicineId,
                reminderTime = reminderEntity.nextReminderTime,
                takenTime = System.currentTimeMillis(),
                status = LogStatus.TAKEN
            )
            db.medicationLogDao().insertLog(log)

            // Update inventory
            val inventory = db.inventoryDao().getByMedicineId(reminderEntity.medicineId)
            if (inventory != null && inventory.currentStock > 0) {
                db.inventoryDao().upsert(inventory.copy(currentStock = inventory.currentStock - 1))
            }

            // Calculate and update next reminder time
            val reminder = ir.danialchoopan.medimate.domain.model.Reminder(
                id = reminderEntity.id,
                medicineId = reminderEntity.medicineId,
                intervalType = reminderEntity.intervalType,
                intervalValue = reminderEntity.intervalValue,
                nextReminderTime = reminderEntity.nextReminderTime,
                isActive = reminderEntity.isActive,
                cycleOnDays = reminderEntity.cycleOnDays,
                cycleOffDays = reminderEntity.cycleOffDays
            )
            val nextTime = ReminderScheduler.calculateNextReminderTime(reminder, reminder.nextReminderTime)
            db.reminderDao().updateReminder(reminderEntity.copy(nextReminderTime = nextTime))

            // Schedule next alarm
            val medicine = db.medicineDao().getMedicineById(reminderEntity.medicineId)
            if (medicine != null) {
                ReminderScheduler.scheduleReminder(context, reminder.copy(nextReminderTime = nextTime), medicine.name)
            }
        }

        // Update the widget
        MediMateWidget().update(context, glanceId)
    }
}

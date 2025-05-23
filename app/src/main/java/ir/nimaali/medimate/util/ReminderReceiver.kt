package ir.nimaali.medimate.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ir.nimaali.medimate.app.MedicineApplication
import ir.nimaali.medimate.data.table.IntervalType
import ir.nimaali.medimate.viewmodel.MedicineViewModel
import ir.nimaali.medimate.viewmodel.MedicineViewModelFactory

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra("reminder_id", -1)
        if (reminderId == -1) return
        val medicineName = intent.getStringExtra("medicine_name") ?: ""
        val intervalType = IntervalType.valueOf(
            intent.getStringExtra("interval_type") ?: IntervalType.HOURS.name
        )
        val intervalValue = intent.getIntExtra("interval_value", 6)


        val app = context.applicationContext as MedicineApplication
        val reminderManager = ReminderManager(
            reminderDao = app.database.reminderDao(),
            context = context
        )

        // 1. نمایش نوتیفیکیشن
        NotificationUtils.showNotification(
            context = context,
            medicineName = medicineName,
            reminderTime = System.currentTimeMillis()
        )

        reminderManager.updateNextReminderTime(reminderId)
    }
}
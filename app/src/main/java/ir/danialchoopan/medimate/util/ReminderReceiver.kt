package ir.nimaali.medimate.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ir.nimaali.medimate.app.MedicineApplication
import ir.nimaali.medimate.data.table.IntervalType
import ir.nimaali.medimate.ui.ReminderScheduler
import ir.nimaali.medimate.viewmodel.MedicineViewModel
import ir.nimaali.medimate.viewmodel.MedicineViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra("reminder_id", -1)
        if (reminderId == -1) return

        val medicineName = intent.getStringExtra("medicine_name") ?: ""

        // نوتیفیکیشن رو نمایش بده
        NotificationUtils.showNotification(
            context = context,
            medicineName = medicineName,
            reminderTime = System.currentTimeMillis()
        )

        // زمان بعدی رو آپدیت کن و همون reminder جدید رو استفاده کن
        val app = context.applicationContext as MedicineApplication
        val reminderManager = ReminderManager(
            reminderDao = app.database.reminderDao(),
            context = context
        )

        CoroutineScope(Dispatchers.IO).launch {
            val updatedReminder = reminderManager.updateNextReminderTime(reminderId)

            updatedReminder?.let {
                // نوبت بعدی رو برنامه‌ریزی کن
                ReminderScheduler.scheduleRepeatingReminder(
                    context = context,
                    reminder = it,
                    medicineName = medicineName
                )
            }
        }
    }
}

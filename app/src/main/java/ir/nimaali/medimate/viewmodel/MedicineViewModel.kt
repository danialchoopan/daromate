package ir.nimaali.medimate.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import ir.nimaali.medimate.data.dao.MedicineDao
import ir.nimaali.medimate.data.dao.ReminderDao
import ir.nimaali.medimate.data.table.IntervalType
import ir.nimaali.medimate.data.table.Medicine
import ir.nimaali.medimate.data.table.Reminder
import ir.nimaali.medimate.data.workers.ReminderWorker
import ir.nimaali.medimate.ui.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MedicineViewModel(
    private val medicineDao: MedicineDao,
    private val reminderDao: ReminderDao,
    private val context: Context,
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

    companion object {
        fun provideFactory(
            medicineDao: MedicineDao,
            reminderDao: ReminderDao,
            context: Context
        ): ViewModelProvider.Factory {
            return MedicineViewModelFactory(medicineDao, reminderDao, context)
        }
    }

    fun addMedicine(medicine: Medicine, interval: Pair<IntervalType, Int>) {
        viewModelScope.launch {
            // 1. ذخیره دارو در دیتابیس
            val medicineId = medicineDao.insert(medicine).toInt()

            // 2. محاسبه زمان اولین یادآوری
            val firstReminderTime = calculateNextReminderTime(
                startTime = medicine.startDate,
                intervalType = interval.first,
                intervalValue = interval.second
            )

            // 3. ایجاد یادآوری
            val reminder = Reminder(
                medicineId = medicineId,
                intervalType = interval.first,
                intervalValue = interval.second,
                nextReminderTime = firstReminderTime
            )

            // 4. ذخیره یادآوری در دیتابیس
            val reminderId = reminderDao.insert(reminder).toInt()

            // 5. زمان‌بندی اولین یادآوری
            ReminderScheduler.scheduleRepeatingReminder(
                context = context,
                reminder = reminder.copy(id = reminderId),
                medicineName = medicine.name
            )
        }
    }

    fun addReminderForMedicine(medicineId: Int, interval: Pair<IntervalType, Int>) {
        viewModelScope.launch {
            // 1. دریافت دارو از دیتابیس
            val medicine = medicineDao.getMedicineById(medicineId).first()
                ?: throw Exception("دارو یافت نشد")

            // 2. محاسبه زمان اولین یادآوری
            val firstReminderTime = calculateNextReminderTime(
                startTime = System.currentTimeMillis(), // شروع از همین الان
                intervalType = interval.first,
                intervalValue = interval.second
            )

            // 3. ایجاد یادآوری
            val reminder = Reminder(
                medicineId = medicineId,
                intervalType = interval.first,
                intervalValue = interval.second,
                nextReminderTime = firstReminderTime,
                isActive = true
            )

            // 4. ذخیره یادآوری در دیتابیس
            val reminderId = reminderDao.insert(reminder).toInt()

            // 5. زمان‌بندی اولین یادآوری
            ReminderScheduler.scheduleRepeatingReminder(
                context = context,
                reminder = reminder.copy(id = reminderId),
                medicineName = medicine.name
            )


        }
    }

    fun cancelReminderById(reminderId: Int) {
        viewModelScope.launch {
            // 1. دریافت تمام یادآوری‌های مربوط به این دارو
            val reminders = reminderDao.getReminderById(reminderId).first()
            if (reminders != null) {
                reminders.isActive = false
                reminderDao.update(reminders)
            }
            if (reminders != null) {
                ReminderScheduler.cancelReminder(context, reminders.id)
            }

        }
    }

    fun deleteReminderById(reminderId: Int) {
        viewModelScope.launch {
            // 1. دریافت تمام یادآوری‌های مربوط به این دارو
            val reminders = reminderDao.getReminderById(reminderId).first()
            if (reminders != null) {
                reminders.isActive = false
                reminderDao.update(reminders)
            }
            if (reminders != null) {
                ReminderScheduler.cancelReminder(context, reminders.id)
            }
            reminderDao.delete(reminderId)
        }
    }

    fun enableReminderById(reminderId: Int) {
        viewModelScope.launch {
            // دریافت یادآوری از دیتابیس
            val reminder = reminderDao.getReminderById(reminderId).first()

            if (reminder != null) {
                // 1. فعال کردن در دیتابیس
                val updatedReminder = reminder.copy(isActive = true)
                reminderDao.update(updatedReminder)

                // 2. زمان‌بندی مجدد نوتیفیکیشن
                val medicine = medicineDao.getMedicineById(reminder.medicineId).first()
                if (medicine != null) {
                    ReminderScheduler.scheduleReminder(
                        context = context,
                        reminder = updatedReminder,
                        medicineName = medicine.name
                    )
                }
            }
        }
    }

    suspend fun deleteMedicineAndReminders(medicineId: Int) {
        // 1. ابتدا تمام یادآوری‌های مربوط به این دارو را پیدا کنید
        val reminders = reminderDao.getRemindersForMedicine(medicineId).first()

        // 2. لغو تمام نوتیفیکیشن‌های مربوطه
        reminders.forEach { reminder ->
            ReminderScheduler.cancelReminder(context, reminder.id)
        }

        // 3. حذف تمام یادآوری‌ها از دیتابیس
        reminderDao.deleteRemindersForMedicine(medicineId)

        // 4. در نهایت دارو را حذف کنید
        medicineDao.delete(medicineId)
    }
    private fun calculateNextReminderTime(
        startTime: Long,
        intervalType: IntervalType,
        intervalValue: Int
    ): Long {
        val intervalMillis = when (intervalType) {
            IntervalType.MINUTES -> intervalValue * 60 * 1000L
            IntervalType.HOURS -> intervalValue * 60 * 60 * 1000L
            IntervalType.DAYS -> intervalValue * 24 * 60 * 60 * 1000L
            IntervalType.WEEKS -> intervalValue * 7 * 24 * 60 * 60 * 1000L
        }

        val now = System.currentTimeMillis()
        if (startTime > now) {
            return startTime
        }

        // محاسبه اولین زمان بعدی بعد از زمان حال
        val passedIntervals = (now - startTime) / intervalMillis + 1
        return startTime + passedIntervals * intervalMillis
    }

//    private fun calculateNextReminderTime(
//        startTime: Long,
//        intervalType: IntervalType,
//        intervalValue: Int,
//    ): Long {
//        return when (intervalType) {
//            IntervalType.MINUTES -> startTime + intervalValue * 60 * 1000L
//            IntervalType.HOURS -> startTime + intervalValue * 60 * 60 * 1000L
//            IntervalType.DAYS -> startTime + intervalValue * 24 * 60 * 60 * 1000L
//            IntervalType.WEEKS -> startTime + intervalValue * 7 * 24 * 60 * 60 * 1000L
//        }
//    }
}

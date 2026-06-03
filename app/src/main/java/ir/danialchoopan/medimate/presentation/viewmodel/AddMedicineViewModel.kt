package ir.danialchoopan.medimate.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.danialchoopan.medimate.domain.model.Inventory
import ir.danialchoopan.medimate.domain.model.Medicine
import ir.danialchoopan.medimate.domain.model.Reminder
import ir.danialchoopan.medimate.domain.usecase.AddMedicineUseCase
import ir.danialchoopan.medimate.util.ReminderScheduler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMedicineViewModel @Inject constructor(
    private val addMedicineUseCase: AddMedicineUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    fun addMedicine(medicine: Medicine, reminders: List<Reminder>, inventory: Inventory?) {
        viewModelScope.launch {
            addMedicineUseCase(medicine, reminders, inventory)
            // Schedule the reminders
            reminders.forEach { reminder ->
                ReminderScheduler.scheduleReminder(context, reminder, medicine.name)
            }
        }
    }
}

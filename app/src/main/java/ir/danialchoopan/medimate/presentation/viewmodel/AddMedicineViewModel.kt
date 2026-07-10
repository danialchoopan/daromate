package ir.danialchoopan.medimate.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.danialchoopan.medimate.domain.model.DrugInteraction
import ir.danialchoopan.medimate.domain.model.Inventory
import ir.danialchoopan.medimate.domain.model.Medicine
import ir.danialchoopan.medimate.domain.model.Reminder
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import ir.danialchoopan.medimate.domain.repository.ReminderRepository
import ir.danialchoopan.medimate.domain.usecase.AddMedicineUseCase
import ir.danialchoopan.medimate.domain.usecase.CheckDrugInteractionsUseCase
import ir.danialchoopan.medimate.util.ReminderScheduler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMedicineViewModel @Inject constructor(
    private val addMedicineUseCase: AddMedicineUseCase,
    private val medicineRepository: MedicineRepository,
    private val reminderRepository: ReminderRepository,
    private val checkDrugInteractionsUseCase: CheckDrugInteractionsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _interactions = MutableStateFlow<List<DrugInteraction>>(emptyList())
    val interactions: StateFlow<List<DrugInteraction>> = _interactions

    private var interactionCheckJob: Job? = null

    fun addMedicine(medicine: Medicine, reminders: List<Reminder>, inventory: Inventory?) {
        viewModelScope.launch {
            val savedReminders = addMedicineUseCase(medicine, reminders, inventory)
            savedReminders.forEach { reminder ->
                ReminderScheduler.scheduleReminder(context, reminder, medicine.name)
            }
        }
    }

    fun checkInteractions(drugName: String) {
        interactionCheckJob?.cancel()
        interactionCheckJob = viewModelScope.launch {
            delay(300) // debounce
            _interactions.value = checkDrugInteractionsUseCase(drugName)
        }
    }

    fun clearInteractions() {
        _interactions.value = emptyList()
    }

    fun getMedicineById(id: Int): StateFlow<Medicine?> {
        val state = MutableStateFlow<Medicine?>(null)
        viewModelScope.launch {
            state.value = medicineRepository.getMedicineById(id)
        }
        return state
    }

    fun updateMedicine(medicine: Medicine) {
        viewModelScope.launch {
            medicineRepository.updateMedicine(medicine)
        }
    }

    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch {
            medicineRepository.deleteMedicine(medicine)
        }
    }
}

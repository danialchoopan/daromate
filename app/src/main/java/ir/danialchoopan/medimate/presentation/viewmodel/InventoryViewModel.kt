package ir.danialchoopan.medimate.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.danialchoopan.medimate.domain.repository.InventoryRepository
import ir.danialchoopan.medimate.domain.model.InventoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    private val _inventoryItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventoryItems: StateFlow<List<InventoryItem>> = _inventoryItems

    init {
        loadInventory()
    }

    private fun loadInventory() {
        viewModelScope.launch {
            inventoryRepository.getAllInventory().collect { items ->
                _inventoryItems.value = items
            }
        }
    }

    fun addInventory(medicineName: String, currentStock: Int, lowStockThreshold: Int) {
        viewModelScope.launch {
            inventoryRepository.addInventory(medicineName, currentStock, lowStockThreshold)
        }
    }

    fun increaseStock(medicineId: Int) {
        viewModelScope.launch {
            inventoryRepository.increaseStock(medicineId)
        }
    }

    fun decreaseStock(medicineId: Int) {
        viewModelScope.launch {
            inventoryRepository.decreaseStock(medicineId)
        }
    }

    fun deleteInventory(medicineId: Int) {
        viewModelScope.launch {
            inventoryRepository.deleteInventory(medicineId)
        }
    }
}

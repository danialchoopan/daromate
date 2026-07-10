package ir.danialchoopan.medimate.domain.usecase

import ir.danialchoopan.medimate.domain.model.DrugInteraction
import ir.danialchoopan.medimate.domain.repository.DrugInteractionRepository
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckDrugInteractionsUseCase @Inject constructor(
    private val interactionRepository: DrugInteractionRepository,
    private val medicineRepository: MedicineRepository
) {
    suspend operator fun invoke(newDrugName: String): List<DrugInteraction> {
        if (newDrugName.isBlank()) return emptyList()

        val activeMedicines = medicineRepository.getAllMedicines().first()
        val allInteractions = mutableListOf<DrugInteraction>()

        for (active in activeMedicines) {
            allInteractions += interactionRepository.getInteractionsForDrug(active.name)
        }

        return allInteractions.filter { interaction ->
            interaction.drugA.equals(newDrugName, ignoreCase = true) ||
                interaction.drugB.equals(newDrugName, ignoreCase = true)
        }.distinctBy { interaction ->
            val a = interaction.drugA.lowercase()
            val b = interaction.drugB.lowercase()
            minOf(a, b) to maxOf(a, b)
        }
    }
}

package ir.danialchoopan.medimate.domain.usecase

import ir.danialchoopan.medimate.domain.model.DrugInteraction
import ir.danialchoopan.medimate.domain.repository.DrugInteractionRepository
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * CheckDrugInteractionsUseCase - Checks for drug interactions
 *
 * When a user types a medicine name, this use case:
 * 1. Gets all currently active medicines
 * 2. Queries the interaction database for each
 * 3. Returns any conflicts with the new drug
 *
 * Used in AddMedicineScreen to show real-time warnings
 */
class CheckDrugInteractionsUseCase @Inject constructor(
    private val interactionRepository: DrugInteractionRepository,
    private val medicineRepository: MedicineRepository
) {
    suspend operator fun invoke(newDrugName: String): List<DrugInteraction> {
        if (newDrugName.isBlank()) return emptyList()

        // Get all active medicines from database
        val activeMedicines = medicineRepository.getAllMedicines().first()
        val allInteractions = mutableListOf<DrugInteraction>()

        // Check each active medicine for interactions with the new drug
        for (active in activeMedicines) {
            allInteractions += interactionRepository.getInteractionsForDrug(active.name)
        }

        // Filter to only interactions involving the new drug, remove duplicates
        return allInteractions.filter { interaction ->
            interaction.drugA.equals(newDrugName, ignoreCase = true) ||
                interaction.drugB.equals(newDrugName, ignoreCase = true)
        }.distinctBy { interaction ->
            // Deduplicate bidirectional matches (A-B and B-A are the same)
            val a = interaction.drugA.lowercase()
            val b = interaction.drugB.lowercase()
            minOf(a, b) to maxOf(a, b)
        }
    }
}

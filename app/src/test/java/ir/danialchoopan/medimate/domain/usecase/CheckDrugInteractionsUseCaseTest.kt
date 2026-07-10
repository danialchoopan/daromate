package ir.danialchoopan.medimate.domain.usecase

import io.mockk.coEvery
import io.mockk.mockk
import ir.danialchoopan.medimate.domain.model.DrugInteraction
import ir.danialchoopan.medimate.domain.model.InteractionSeverity
import ir.danialchoopan.medimate.domain.model.Medicine
import ir.danialchoopan.medimate.domain.repository.DrugInteractionRepository
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CheckDrugInteractionsUseCaseTest {

    private lateinit var interactionRepository: DrugInteractionRepository
    private lateinit var medicineRepository: MedicineRepository
    private lateinit var useCase: CheckDrugInteractionsUseCase

    @Before
    fun setup() {
        interactionRepository = mockk()
        medicineRepository = mockk()
        useCase = CheckDrugInteractionsUseCase(interactionRepository, medicineRepository)
    }

    @Test
    fun `returns interactions for new drug matching active medicine`() = runTest {
        val activeMedicines = listOf(
            Medicine(id = 1, name = "Warfarin", description = "", dosage = "5mg", form = "Tablet", instruction = "", reason = "")
        )
        coEvery { medicineRepository.getAllMedicines() } returns flowOf(activeMedicines)
        coEvery { interactionRepository.getInteractionsForDrug("Warfarin") } returns listOf(
            DrugInteraction("Warfarin", "Aspirin", InteractionSeverity.SEVERE, "Bleeding risk")
        )

        val result = useCase("Aspirin")

        assertEquals(1, result.size)
        assertEquals("Warfarin", result[0].drugA)
        assertEquals("Aspirin", result[0].drugB)
    }

    @Test
    fun `returns empty when no active medicines share interaction`() = runTest {
        val activeMedicines = listOf(
            Medicine(id = 1, name = "Vitamin C", description = "", dosage = "500mg", form = "Tablet", instruction = "", reason = "")
        )
        coEvery { medicineRepository.getAllMedicines() } returns flowOf(activeMedicines)
        coEvery { interactionRepository.getInteractionsForDrug("Vitamin C") } returns emptyList()

        val result = useCase("Aspirin")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `deduplicates bidirectional matches`() = runTest {
        val activeMedicines = listOf(
            Medicine(id = 1, name = "Warfarin", description = "", dosage = "5mg", form = "Tablet", instruction = "", reason = "")
        )
        coEvery { medicineRepository.getAllMedicines() } returns flowOf(activeMedicines)
        coEvery { interactionRepository.getInteractionsForDrug("Warfarin") } returns listOf(
            DrugInteraction("Warfarin", "Aspirin", InteractionSeverity.SEVERE, "Bleeding risk"),
            DrugInteraction("Aspirin", "Warfarin", InteractionSeverity.SEVERE, "Bleeding risk")
        )

        val result = useCase("Aspirin")

        assertEquals(1, result.size)
    }

    @Test
    fun `case insensitive matching`() = runTest {
        val activeMedicines = listOf(
            Medicine(id = 1, name = "warfarin", description = "", dosage = "5mg", form = "Tablet", instruction = "", reason = "")
        )
        coEvery { medicineRepository.getAllMedicines() } returns flowOf(activeMedicines)
        coEvery { interactionRepository.getInteractionsForDrug("warfarin") } returns listOf(
            DrugInteraction("Warfarin", "Aspirin", InteractionSeverity.SEVERE, "Bleeding risk")
        )

        val result = useCase("aspirin")

        assertEquals(1, result.size)
    }

    @Test
    fun `returns empty for blank drug name`() = runTest {
        val result = useCase("")
        assertTrue(result.isEmpty())
    }
}

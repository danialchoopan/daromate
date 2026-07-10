package ir.danialchoopan.medimate.domain.usecase

import android.content.Context
import android.net.Uri
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import ir.danialchoopan.medimate.domain.model.LogStatus
import ir.danialchoopan.medimate.domain.model.MedicationLog
import ir.danialchoopan.medimate.domain.model.Medicine
import ir.danialchoopan.medimate.domain.repository.LogRepository
import ir.danialchoopan.medimate.domain.repository.MedicineRepository
import ir.danialchoopan.medimate.util.CsvExporter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ExportHistoryUseCaseTest {

    private lateinit var logRepository: LogRepository
    private lateinit var medicineRepository: MedicineRepository
    private lateinit var useCase: ExportHistoryUseCase

    @Before
    fun setup() {
        logRepository = mockk()
        medicineRepository = mockk()
        useCase = ExportHistoryUseCase(logRepository, medicineRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `export returns uri from CsvExporter`() = runTest {
        val context = mockk<Context>()
        val expectedUri = mockk<Uri>()

        coEvery { logRepository.getAllLogs() } returns flowOf(emptyList())
        coEvery { medicineRepository.getAllMedicines() } returns flowOf(emptyList())

        mockkStatic(CsvExporter::class)
        coEvery { CsvExporter.exportMedicationHistory(context, logRepository, medicineRepository) } returns expectedUri

        val result = useCase(context)

        assertEquals(expectedUri, result)
    }
}

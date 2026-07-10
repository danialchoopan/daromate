package ir.danialchoopan.medimate.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ir.danialchoopan.medimate.data.local.dao.DrugInteractionDao
import ir.danialchoopan.medimate.data.local.entities.DrugInteractionEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrugInteractionDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: DrugInteractionDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        dao = database.drugInteractionDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `insertAll and count works correctly`() = runTest {
        val interactions = listOf(
            DrugInteractionEntity(drugA = "Warfarin", drugB = "Aspirin", severity = "SEVERE", description = "Bleeding risk"),
            DrugInteractionEntity(drugA = "Metformin", drugB = "Ibuprofen", severity = "MODERATE", description = "Kidney risk")
        )

        dao.insertAll(interactions)

        val count = dao.count()
        assertEquals(2, count)
    }

    @Test
    fun `getInteractionsForDrug returns bidirectional matches`() = runTest {
        val interactions = listOf(
            DrugInteractionEntity(drugA = "Warfarin", drugB = "Aspirin", severity = "SEVERE", description = "Bleeding risk")
        )
        dao.insertAll(interactions)

        val resultForA = dao.getInteractionsForDrug("Warfarin")
        val resultForB = dao.getInteractionsForDrug("Aspirin")

        assertEquals(1, resultForA.size)
        assertEquals(1, resultForB.size)
    }

    @Test
    fun `getInteractionsForDrug is case insensitive`() = runTest {
        val interactions = listOf(
            DrugInteractionEntity(drugA = "Warfarin", drugB = "Aspirin", severity = "SEVERE", description = "Bleeding risk")
        )
        dao.insertAll(interactions)

        val result = dao.getInteractionsForDrug("warfarin")

        assertEquals(1, result.size)
    }

    @Test
    fun `getInteractionsForDrug returns empty when no match`() = runTest {
        val interactions = listOf(
            DrugInteractionEntity(drugA = "Warfarin", drugB = "Aspirin", severity = "SEVERE", description = "Bleeding risk")
        )
        dao.insertAll(interactions)

        val result = dao.getInteractionsForDrug("Vitamin C")

        assertTrue(result.isEmpty())
    }
}

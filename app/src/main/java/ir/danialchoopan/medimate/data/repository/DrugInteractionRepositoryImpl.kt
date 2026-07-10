package ir.danialchoopan.medimate.data.repository

import ir.danialchoopan.medimate.data.local.dao.DrugInteractionDao
import ir.danialchoopan.medimate.domain.model.DrugInteraction
import ir.danialchoopan.medimate.domain.model.InteractionSeverity
import ir.danialchoopan.medimate.domain.repository.DrugInteractionRepository

class DrugInteractionRepositoryImpl(
    private val dao: DrugInteractionDao
) : DrugInteractionRepository {
    override suspend fun getInteractionsForDrug(drugName: String): List<DrugInteraction> =
        dao.getInteractionsForDrug(drugName).map {
            DrugInteraction(
                drugA = it.drugA,
                drugB = it.drugB,
                severity = InteractionSeverity.valueOf(it.severity),
                description = it.description
            )
        }
}

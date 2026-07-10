package ir.danialchoopan.medimate.domain.repository

import ir.danialchoopan.medimate.domain.model.DrugInteraction

interface DrugInteractionRepository {
    suspend fun getInteractionsForDrug(drugName: String): List<DrugInteraction>
}

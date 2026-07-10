package ir.danialchoopan.medimate.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.danialchoopan.medimate.data.local.dao.DrugInteractionDao
import ir.danialchoopan.medimate.data.local.entities.DrugInteractionEntity

object DrugInteractionSeeder {

    private data class DrugInteractionDto(
        val drugA: String,
        val drugB: String,
        val severity: String,
        val description: String
    )

    suspend fun seedIfNeeded(context: Context, dao: DrugInteractionDao) {
        if (dao.count() > 0) return

        val json = context.assets.open("drug_interactions.json").bufferedReader().readText()
        val type = object : TypeToken<List<DrugInteractionDto>>() {}.type
        val dtos: List<DrugInteractionDto> = Gson().fromJson(json, type)

        val entities = dtos.map { dto ->
            DrugInteractionEntity(
                drugA = dto.drugA,
                drugB = dto.drugB,
                severity = dto.severity,
                description = dto.description
            )
        }

        dao.insertAll(entities)
    }
}

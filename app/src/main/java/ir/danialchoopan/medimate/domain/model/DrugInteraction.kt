package ir.danialchoopan.medimate.domain.model

data class DrugInteraction(
    val drugA: String,
    val drugB: String,
    val severity: InteractionSeverity,
    val description: String
)

enum class InteractionSeverity {
    SEVERE,
    MODERATE,
    MILD
}

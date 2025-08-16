package com.cconstruct.construction.models.responses
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class EvidenceResponse(
    val id: Long,
    val fileName: String,
    val progressId: Long
)
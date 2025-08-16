package com.cconstruct.construction.models.responses

import com.cconstruct.construction.models.dtos.EvidenceDto
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class EvidenceListResponse(
    val images: List<EvidenceDto>,
    val total: Long
)
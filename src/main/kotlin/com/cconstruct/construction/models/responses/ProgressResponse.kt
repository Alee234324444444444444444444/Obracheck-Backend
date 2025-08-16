package com.cconstruct.construction.models.responses

import java.time.LocalDateTime
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ProgressResponse(
    val id: Long,
    val description: String,
    val date: LocalDateTime,
    val site: SiteSummaryResponse,
    val worker: WorkerSummaryResponse,
    val evidences: List<EvidenceSummaryResponse>
)
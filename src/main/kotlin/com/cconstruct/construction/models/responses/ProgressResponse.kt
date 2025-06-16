package com.cconstruct.construction.models.responses

import java.time.LocalDateTime

data class ProgressResponse(
    val id: Long,
    val description: String,
    val date: LocalDateTime,
    val site: SiteSummaryResponse,
    val worker: WorkerSummaryResponse,
    val evidences: List<EvidenceSummaryResponse>
)
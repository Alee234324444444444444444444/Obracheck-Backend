package com.cconstruct.construction.models.responses

data class WorkerResponse(
    val id: Long,
    val name: String,
    val role: String,
    val ci: String,
    val site: SiteSummaryResponse
)
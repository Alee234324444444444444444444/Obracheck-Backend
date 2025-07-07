package com.cconstruct.construction.models.responses

data class SiteResponse(
    val id: Long,
    val name: String,
    val address: String,
    val workers: List<WorkerSummaryResponse>,
    val progresses: List<ProgressSummaryResponse>,
    val user: UserResponse
)
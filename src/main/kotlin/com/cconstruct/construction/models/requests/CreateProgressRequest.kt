package com.cconstruct.construction.models.requests

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class CreateProgressRequest(
    val description: String,
    val date: LocalDateTime,

    @JsonProperty("site_id")
    val siteId: Long,

    @JsonProperty("worker_id")
    val workerId: Long
)

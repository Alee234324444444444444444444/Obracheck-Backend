package com.cconstruct.construction.models.requests

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CreateProgressRequest(
    val description: String,
    val date: LocalDateTime,

    @JsonProperty("site_id")
    val siteId: Long,

    @JsonProperty("worker_id")
    val workerId: Long
)

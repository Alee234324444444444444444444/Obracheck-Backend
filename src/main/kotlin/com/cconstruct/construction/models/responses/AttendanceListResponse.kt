package com.cconstruct.construction.models.responses

import java.time.LocalDate
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AttendanceListResponse(

    val siteId: Long,
    val siteName: String,
    val date: LocalDate,
    val items: List<AttendanceResponse>
)
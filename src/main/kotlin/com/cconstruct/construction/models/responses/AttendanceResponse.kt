package com.cconstruct.construction.models.responses

import com.cconstruct.construction.models.entities.AttendanceStatus
import java.time.LocalDate
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AttendanceResponse(
    val id: Long,


    val workerId: Long,

    val workerName: String,


    val siteId: Long,

    val siteName: String,

    val date: LocalDate,
    val status: AttendanceStatus
)
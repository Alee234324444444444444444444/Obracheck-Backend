package com.cconstruct.construction.models.requests

import com.cconstruct.construction.models.entities.AttendanceStatus
import java.time.LocalDate
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AttendanceBulkUpsertRequest(

    val siteId: Long,

    val date: LocalDate? = null,

    val items: List<Item>
) {
    data class Item(
        val workerId: Long,
        val status: AttendanceStatus
    )
}
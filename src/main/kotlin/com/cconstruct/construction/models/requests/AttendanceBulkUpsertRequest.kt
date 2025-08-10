package com.cconstruct.construction.models.requests

import com.cconstruct.construction.models.entities.AttendanceStatus
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

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
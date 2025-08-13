package com.cconstruct.construction.models.responses

import java.time.LocalDate

data class AttendanceListResponse(

    val siteId: Long,
    val siteName: String,
    val date: LocalDate,
    val items: List<AttendanceResponse>
)
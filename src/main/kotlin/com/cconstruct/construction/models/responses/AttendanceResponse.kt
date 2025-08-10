package com.cconstruct.construction.models.responses

import com.cconstruct.construction.models.entities.AttendanceStatus
import java.time.LocalDate

data class AttendanceResponse(
    val id: Long,


    val workerId: Long,

    val workerName: String,


    val siteId: Long,

    val siteName: String,

    val date: LocalDate,
    val status: AttendanceStatus
)
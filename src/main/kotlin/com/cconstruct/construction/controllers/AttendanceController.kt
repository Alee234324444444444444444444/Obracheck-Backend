package com.cconstruct.construction.controllers

import com.cconstruct.construction.constants.Routes
import com.cconstruct.construction.models.requests.AttendanceBulkUpsertRequest
import com.cconstruct.construction.models.responses.AttendanceListResponse
import com.cconstruct.construction.models.responses.AttendanceResponse
import com.cconstruct.construction.services.AttendanceService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.ISO
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping(Routes.ATTENDANCES)
class AttendanceController(
    private val attendanceService: AttendanceService
) {

    @GetMapping("/site/{siteId}/date/{date}")
    fun listBySiteAndDate(
        @PathVariable siteId: Long,
        @PathVariable @DateTimeFormat(iso = ISO.DATE) date: LocalDate
    ): AttendanceListResponse =
        attendanceService.listBySiteAndDate(siteId, date)


    @PostMapping("/bulk")
    fun upsertBulkAttendance(
        @RequestBody request: AttendanceBulkUpsertRequest
    ): List<AttendanceResponse> =
        attendanceService.upsertBulkAttendance(request)
}

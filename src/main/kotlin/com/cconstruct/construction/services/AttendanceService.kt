package com.cconstruct.construction.services

import com.cconstruct.construction.exceptions.SiteNotFoundException
import com.cconstruct.construction.exceptions.WorkerNotFoundException
import com.cconstruct.construction.mappers.AttendanceMapper
import com.cconstruct.construction.models.entities.Attendance
import com.cconstruct.construction.models.requests.AttendanceBulkUpsertRequest
import com.cconstruct.construction.models.responses.AttendanceListResponse
import com.cconstruct.construction.models.responses.AttendanceResponse
import com.cconstruct.construction.repositories.AttendanceRepository
import com.cconstruct.construction.repositories.SiteRepository
import com.cconstruct.construction.repositories.WorkerRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AttendanceService(
    private val attendanceRepository: AttendanceRepository,
    private val workerRepository: WorkerRepository,
    private val siteRepository: SiteRepository,
    private val attendanceMapper: AttendanceMapper
) {

    fun listBySiteAndDate(siteId: Long, date: LocalDate): AttendanceListResponse {
        val site = siteRepository.findById(siteId)
            .orElseThrow { SiteNotFoundException("Site with ID $siteId not found.") }

        val items: List<AttendanceResponse> = attendanceRepository
            .findBySiteIdAndDate(siteId, date)
            .map { attendanceMapper.toResponse(it) }

        return AttendanceListResponse(
            siteId = site.id,
            siteName = site.name,
            date = date,
            items = items
        )
    }


    fun upsertBulkAttendance(request: AttendanceBulkUpsertRequest): List<AttendanceResponse> {
        val date = request.date ?: LocalDate.now()
        val site = siteRepository.findById(request.siteId)
            .orElseThrow { SiteNotFoundException("Site with ID ${request.siteId} not found.") }

        return request.items.map { item ->
            val worker = workerRepository.findById(item.workerId)
                .orElseThrow { WorkerNotFoundException("Worker with ID ${item.workerId} not found.") }


            val attendance: Attendance = attendanceRepository
                .findByWorkerIdAndDate(worker.id, date)
                ?.apply {
                    this.site = site
                    this.status = item.status
                }
                ?: attendanceMapper.toEntity(
                    worker = worker,
                    site = site,
                    date = date,
                    status = item.status
                )

            attendanceMapper.toResponse(attendanceRepository.save(attendance))
        }
    }
}

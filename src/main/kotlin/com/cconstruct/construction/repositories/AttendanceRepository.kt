package com.cconstruct.construction.repositories

import com.cconstruct.construction.models.entities.Attendance
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface AttendanceRepository : JpaRepository<Attendance, Long> {
    fun findBySiteIdAndDate(siteId: Long, date: LocalDate): List<Attendance>
    fun findByWorkerIdAndDate(workerId: Long, date: LocalDate): Attendance?
}

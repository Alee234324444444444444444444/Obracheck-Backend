package com.cconstruct.construction.mappers



import com.cconstruct.construction.models.entities.Attendance
import com.cconstruct.construction.models.entities.AttendanceStatus
import com.cconstruct.construction.models.entities.Site
import com.cconstruct.construction.models.entities.Worker
import com.cconstruct.construction.models.responses.AttendanceResponse
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class AttendanceMapper : BaseMapper<Attendance, AttendanceResponse> {

    override fun toResponse(entity: Attendance): AttendanceResponse {
        return AttendanceResponse(
            id = entity.id,
            workerId = entity.worker.id,
            workerName = entity.worker.name,
            siteId = entity.site.id,
            siteName = entity.site.name,
            date = entity.date,
            status = entity.status
        )
    }

    fun toEntity(
        worker: Worker,
        site: Site,
        date: LocalDate,
        status: AttendanceStatus = AttendanceStatus.NA
    ): Attendance {
        return Attendance(
            worker = worker,
            site = site,
            date = date,
            status = status
        )
    }
}

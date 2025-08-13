package com.cconstruct.construction.services

import com.cconstruct.construction.exceptions.SiteNotFoundException
import com.cconstruct.construction.exceptions.WorkerNotFoundException
import com.cconstruct.construction.mappers.AttendanceMapper
import com.cconstruct.construction.models.entities.Attendance
import com.cconstruct.construction.models.entities.AttendanceStatus
import com.cconstruct.construction.models.entities.Site
import com.cconstruct.construction.models.entities.Worker
import com.cconstruct.construction.models.requests.AttendanceBulkUpsertRequest
import com.cconstruct.construction.models.responses.AttendanceResponse
import com.cconstruct.construction.repositories.AttendanceRepository
import com.cconstruct.construction.repositories.SiteRepository
import com.cconstruct.construction.repositories.WorkerRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.time.LocalDate
import java.util.*

class AttendanceServiceTest {

    private lateinit var attendanceRepository: AttendanceRepository
    private lateinit var workerRepository: WorkerRepository
    private lateinit var siteRepository: SiteRepository
    private lateinit var attendanceMapper: AttendanceMapper

    private lateinit var attendanceService: AttendanceService

    @BeforeEach
    fun setUp() {
        attendanceRepository = mock(AttendanceRepository::class.java)
        workerRepository = mock(WorkerRepository::class.java)
        siteRepository = mock(SiteRepository::class.java)
        attendanceMapper = mock(AttendanceMapper::class.java)

        attendanceService = AttendanceService(
            attendanceRepository,
            workerRepository,
            siteRepository,
            attendanceMapper
        )
    }

    @Test
    fun should_list_by_site_and_date() {
        val siteId = 1L
        val date = LocalDate.of(2025, 8, 10)

        val site = mock(Site::class.java)
        `when`(site.id).thenReturn(siteId)
        `when`(site.name).thenReturn("Obra Norte")

        val attendance1 = mock(Attendance::class.java)
        val attendance2 = mock(Attendance::class.java)

        val response1 = AttendanceResponse(1L, 101L, "Luis",   1L, "Obra Norte", date, AttendanceStatus.PRESENT)
        val response2 = AttendanceResponse(2L, 102L, "Carlos", 1L, "Obra Norte", date, AttendanceStatus.ABSENT)

        `when`(siteRepository.findById(siteId)).thenReturn(Optional.of(site))
        `when`(attendanceRepository.findBySiteIdAndDate(siteId, date)).thenReturn(listOf(attendance1, attendance2))
        `when`(attendanceMapper.toResponse(attendance1)).thenReturn(response1)
        `when`(attendanceMapper.toResponse(attendance2)).thenReturn(response2)

        val result = attendanceService.listBySiteAndDate(siteId, date)

        assertEquals(siteId, result.siteId)
        assertEquals("Obra Norte", result.siteName)
        assertEquals(date, result.date)
        assertEquals(listOf(response1, response2), result.items)

        verify(siteRepository).findById(siteId)
        verify(attendanceRepository).findBySiteIdAndDate(siteId, date)
        verify(attendanceMapper).toResponse(attendance1)
        verify(attendanceMapper).toResponse(attendance2)
        verifyNoMoreInteractions(siteRepository, attendanceRepository, workerRepository, attendanceMapper)
    }

    @Test
    fun should_throw_404_when_site_not_found_on_list() {
        val siteId = 99L
        val date = LocalDate.of(2025, 8, 10)

        `when`(siteRepository.findById(siteId)).thenReturn(Optional.empty())

        assertThrows<SiteNotFoundException> {
            attendanceService.listBySiteAndDate(siteId, date)
        }

        verify(siteRepository).findById(siteId)
        verifyNoMoreInteractions(siteRepository, attendanceRepository, workerRepository, attendanceMapper)
    }

    @Test
    fun should_upsert_bulk_creating_new_records() {
        val date = LocalDate.of(2025, 8, 10)
        val request = AttendanceBulkUpsertRequest(
            siteId = 1L,
            date = date,
            items = listOf(
                AttendanceBulkUpsertRequest.Item(101L, AttendanceStatus.PRESENT),
                AttendanceBulkUpsertRequest.Item(102L, AttendanceStatus.LATE)
            )
        )

        val site = mock(Site::class.java).also {
            `when`(it.id).thenReturn(1L)
            `when`(it.name).thenReturn("Obra Norte")
        }
        val worker1 = mock(Worker::class.java).also { `when`(it.id).thenReturn(101L) }
        val worker2 = mock(Worker::class.java).also { `when`(it.id).thenReturn(102L) }

        val newAttendance1 = mock(Attendance::class.java)
        val newAttendance2 = mock(Attendance::class.java)

        val response1 = AttendanceResponse(1L, 101L, "Luis",   1L, "Obra Norte", date, AttendanceStatus.PRESENT)
        val response2 = AttendanceResponse(2L, 102L, "Carlos", 1L, "Obra Norte", date, AttendanceStatus.LATE)

        `when`(siteRepository.findById(1L)).thenReturn(Optional.of(site))
        `when`(workerRepository.findById(101L)).thenReturn(Optional.of(worker1))
        `when`(workerRepository.findById(102L)).thenReturn(Optional.of(worker2))

        `when`(attendanceRepository.findByWorkerIdAndDate(101L, date)).thenReturn(null)
        `when`(attendanceRepository.findByWorkerIdAndDate(102L, date)).thenReturn(null)

        `when`(attendanceMapper.toEntity(worker1, site, date, AttendanceStatus.PRESENT)).thenReturn(newAttendance1)
        `when`(attendanceMapper.toEntity(worker2, site, date, AttendanceStatus.LATE)).thenReturn(newAttendance2)

        `when`(attendanceRepository.save(newAttendance1)).thenReturn(newAttendance1)
        `when`(attendanceRepository.save(newAttendance2)).thenReturn(newAttendance2)

        `when`(attendanceMapper.toResponse(newAttendance1)).thenReturn(response1)
        `when`(attendanceMapper.toResponse(newAttendance2)).thenReturn(response2)

        val result = attendanceService.upsertBulkAttendance(request)

        assertEquals(listOf(response1, response2), result)

        verify(siteRepository).findById(1L)
        verify(workerRepository).findById(101L)
        verify(workerRepository).findById(102L)
        verify(attendanceRepository).findByWorkerIdAndDate(101L, date)
        verify(attendanceRepository).findByWorkerIdAndDate(102L, date)
        verify(attendanceMapper).toEntity(worker1, site, date, AttendanceStatus.PRESENT)
        verify(attendanceMapper).toEntity(worker2, site, date, AttendanceStatus.LATE)
        verify(attendanceRepository).save(newAttendance1)
        verify(attendanceRepository).save(newAttendance2)
        verify(attendanceMapper).toResponse(newAttendance1)
        verify(attendanceMapper).toResponse(newAttendance2)
        verifyNoMoreInteractions(siteRepository, workerRepository, attendanceRepository, attendanceMapper)
    }

    @Test
    fun should_upsert_bulk_updating_existing_records() {
        val date = LocalDate.of(2025, 8, 10)
        val request = AttendanceBulkUpsertRequest(
            siteId = 1L,
            date = date,
            items = listOf(
                AttendanceBulkUpsertRequest.Item(101L, AttendanceStatus.ABSENT),
                AttendanceBulkUpsertRequest.Item(102L, AttendanceStatus.PRESENT)
            )
        )

        val site = mock(Site::class.java).also {
            `when`(it.id).thenReturn(1L)
            `when`(it.name).thenReturn("Obra Norte")
        }
        val worker1 = mock(Worker::class.java).also { `when`(it.id).thenReturn(101L) }
        val worker2 = mock(Worker::class.java).also { `when`(it.id).thenReturn(102L) }

        val existingAttendance1 = mock(Attendance::class.java)
        val existingAttendance2 = mock(Attendance::class.java)

        val response1 = AttendanceResponse(10L, 101L, "Luis",   1L, "Obra Norte", date, AttendanceStatus.ABSENT)
        val response2 = AttendanceResponse(20L, 102L, "Carlos", 1L, "Obra Norte", date, AttendanceStatus.PRESENT)

        `when`(siteRepository.findById(1L)).thenReturn(Optional.of(site))
        `when`(workerRepository.findById(101L)).thenReturn(Optional.of(worker1))
        `when`(workerRepository.findById(102L)).thenReturn(Optional.of(worker2))

        `when`(attendanceRepository.findByWorkerIdAndDate(101L, date)).thenReturn(existingAttendance1)
        `when`(attendanceRepository.findByWorkerIdAndDate(102L, date)).thenReturn(existingAttendance2)

        `when`(attendanceRepository.save(existingAttendance1)).thenReturn(existingAttendance1)
        `when`(attendanceRepository.save(existingAttendance2)).thenReturn(existingAttendance2)

        `when`(attendanceMapper.toResponse(existingAttendance1)).thenReturn(response1)
        `when`(attendanceMapper.toResponse(existingAttendance2)).thenReturn(response2)

        val result = attendanceService.upsertBulkAttendance(request)

        assertEquals(listOf(response1, response2), result)

        verify(siteRepository).findById(1L)
        verify(workerRepository).findById(101L)
        verify(workerRepository).findById(102L)
        verify(attendanceRepository).findByWorkerIdAndDate(101L, date)
        verify(attendanceRepository).findByWorkerIdAndDate(102L, date)
        verify(attendanceRepository).save(existingAttendance1)
        verify(attendanceRepository).save(existingAttendance2)
        verify(attendanceMapper).toResponse(existingAttendance1)
        verify(attendanceMapper).toResponse(existingAttendance2)
        verifyNoMoreInteractions(siteRepository, workerRepository, attendanceRepository, attendanceMapper)
    }

    @Test
    fun should_throw_404_when_site_not_found_on_bulk() {
        val request = AttendanceBulkUpsertRequest(
            siteId = 999L,
            date = LocalDate.of(2025, 8, 10),
            items = listOf(AttendanceBulkUpsertRequest.Item(1L, AttendanceStatus.PRESENT))
        )

        `when`(siteRepository.findById(999L)).thenReturn(Optional.empty())

        assertThrows<SiteNotFoundException> {
            attendanceService.upsertBulkAttendance(request)
        }

        verify(siteRepository).findById(999L)
        verifyNoMoreInteractions(siteRepository, workerRepository, attendanceRepository, attendanceMapper)
    }

    @Test
    fun should_throw_404_when_worker_not_found_on_bulk() {
        val request = AttendanceBulkUpsertRequest(
            siteId = 1L,
            date = LocalDate.of(2025, 8, 10),
            items = listOf(AttendanceBulkUpsertRequest.Item(123L, AttendanceStatus.PRESENT))
        )

        val site = mock(Site::class.java)
        `when`(siteRepository.findById(1L)).thenReturn(Optional.of(site))
        `when`(workerRepository.findById(123L)).thenReturn(Optional.empty())

        assertThrows<WorkerNotFoundException> {
            attendanceService.upsertBulkAttendance(request)
        }

        verify(siteRepository).findById(1L)
        verify(workerRepository).findById(123L)
        verifyNoMoreInteractions(siteRepository, workerRepository, attendanceRepository, attendanceMapper)
    }

    @Test
    fun should_use_today_when_date_is_null_on_bulk() {
        val today = LocalDate.now()
        val request = AttendanceBulkUpsertRequest(
            siteId = 1L,
            date = null,
            items = listOf(AttendanceBulkUpsertRequest.Item(101L, AttendanceStatus.PRESENT))
        )

        val site = mock(Site::class.java).also {
            `when`(it.id).thenReturn(1L)
            `when`(it.name).thenReturn("Obra Norte")
        }
        val worker = mock(Worker::class.java).also { `when`(it.id).thenReturn(101L) }

        val newAttendance = mock(Attendance::class.java)
        val response = AttendanceResponse(
            id = 1L, workerId = 101L, workerName = "Luis",
            siteId = 1L, siteName = "Obra Norte", date = today, status = AttendanceStatus.PRESENT
        )

        `when`(siteRepository.findById(1L)).thenReturn(Optional.of(site))
        `when`(workerRepository.findById(101L)).thenReturn(Optional.of(worker))
        `when`(attendanceRepository.findByWorkerIdAndDate(101L, today)).thenReturn(null)
        `when`(attendanceMapper.toEntity(worker, site, today, AttendanceStatus.PRESENT)).thenReturn(newAttendance)
        `when`(attendanceRepository.save(newAttendance)).thenReturn(newAttendance)
        `when`(attendanceMapper.toResponse(newAttendance)).thenReturn(response)

        val result = attendanceService.upsertBulkAttendance(request)

        assertEquals(1, result.size)
        assertEquals(response, result[0])


        verify(siteRepository).findById(1L)
        verify(workerRepository).findById(101L)
        verify(attendanceRepository).findByWorkerIdAndDate(101L, today)
        verify(attendanceMapper).toEntity(worker, site, today, AttendanceStatus.PRESENT)
        verify(attendanceRepository).save(newAttendance)
        verify(attendanceMapper).toResponse(newAttendance)
    }

}

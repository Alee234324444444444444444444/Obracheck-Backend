package com.cconstruct.construction.controllers

import com.cconstruct.construction.constants.Routes
import com.cconstruct.construction.exceptions.SiteNotFoundException
import com.cconstruct.construction.exceptions.WorkerNotFoundException
import com.cconstruct.construction.models.entities.AttendanceStatus
import com.cconstruct.construction.models.requests.AttendanceBulkUpsertRequest
import com.cconstruct.construction.models.responses.AttendanceListResponse
import com.cconstruct.construction.models.responses.AttendanceResponse
import com.cconstruct.construction.services.AttendanceService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDate
import kotlin.test.assertEquals

@WebMvcTest(AttendanceController::class)
@Import(AttendanceControllerTest.MockConfig::class)
class AttendanceControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var attendanceService: AttendanceService

    private lateinit var objectMapper: ObjectMapper

    private val BASE_URL = Routes.ATTENDANCES

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Test
    fun should_list_by_site_and_date() {
        val date = LocalDate.of(2025, 8, 10)

        val items = listOf(
            AttendanceResponse(
                id = 1L,
                workerId = 101L,
                workerName = "Luis",
                siteId = 1L,
                siteName = "Obra Norte",
                date = date,
                status = AttendanceStatus.PRESENT
            ),
            AttendanceResponse(
                id = 2L,
                workerId = 102L,
                workerName = "Carlos",
                siteId = 1L,
                siteName = "Obra Norte",
                date = date,
                status = AttendanceStatus.ABSENT
            )
        )

        val response = AttendanceListResponse(
            siteId = 1L,
            siteName = "Obra Norte",
            date = date,
            items = items
        )

        `when`(attendanceService.listBySiteAndDate(1L, date)).thenReturn(response)

        val result = mockMvc.get("$BASE_URL/site/1/date/$date")
            .andExpect {
                status { isOk() }

                jsonPath("$.siteId") { value(1) }
                jsonPath("$.siteName") { value("Obra Norte") }
                jsonPath("$.date") { value(date.toString()) }
                jsonPath("$.items[0].workerId") { value(101) }
                jsonPath("$.items[0].workerName") { value("Luis") }
                jsonPath("$.items[0].siteId") { value(1) }
                jsonPath("$.items[0].siteName") { value("Obra Norte") }
                jsonPath("$.items[0].status") { value("PRESENT") }
                jsonPath("$.items[1].status") { value("ABSENT") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_404_when_site_not_found_on_list() {
        val date = LocalDate.of(2025, 8, 10)

        `when`(attendanceService.listBySiteAndDate(99L, date))
            .thenThrow(SiteNotFoundException("Site with ID 99 not found."))

        val result = mockMvc.get("$BASE_URL/site/99/date/$date")
            .andExpect { status { isNotFound() } }
            .andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_upsert_bulk_attendance() {
        val date = LocalDate.of(2025, 8, 10)

        val request = AttendanceBulkUpsertRequest(
            siteId = 1L,
            date = date,
            items = listOf(
                AttendanceBulkUpsertRequest.Item(workerId = 101L, status = AttendanceStatus.PRESENT),
                AttendanceBulkUpsertRequest.Item(workerId = 102L, status = AttendanceStatus.LATE)
            )
        )

        val serviceResponse = listOf(
            AttendanceResponse(1L, 101L, "Luis",   1L, "Obra Norte", date, AttendanceStatus.PRESENT),
            AttendanceResponse(2L, 102L, "Carlos", 1L, "Obra Norte", date, AttendanceStatus.LATE)
        )

        `when`(attendanceService.upsertBulkAttendance(request)).thenReturn(serviceResponse)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post("$BASE_URL/bulk") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isOk() }
            jsonPath("$[0].workerId") { value(101) }
            jsonPath("$[0].workerName") { value("Luis") }
            jsonPath("$[0].siteId") { value(1) }
            jsonPath("$[0].siteName") { value("Obra Norte") }
            jsonPath("$[0].status") { value("PRESENT") }
            jsonPath("$[1].status") { value("LATE") }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_404_when_site_not_found_on_bulk() {
        val request = AttendanceBulkUpsertRequest(
            siteId = 999L,
            date = LocalDate.of(2025, 8, 10),
            items = listOf(
                AttendanceBulkUpsertRequest.Item(workerId = 1L, status = AttendanceStatus.PRESENT)
            )
        )

        `when`(attendanceService.upsertBulkAttendance(request))
            .thenThrow(SiteNotFoundException("Site with ID 999 not found."))

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post("$BASE_URL/bulk") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect { status { isNotFound() } }
            .andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_return_404_when_worker_not_found_on_bulk() {
        val request = AttendanceBulkUpsertRequest(
            siteId = 1L,
            date = LocalDate.of(2025, 8, 10),
            items = listOf(
                AttendanceBulkUpsertRequest.Item(workerId = 123L, status = AttendanceStatus.PRESENT)
            )
        )

        `when`(attendanceService.upsertBulkAttendance(request))
            .thenThrow(WorkerNotFoundException("Worker with ID 123 not found."))

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post("$BASE_URL/bulk") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect { status { isNotFound() } }
            .andReturn()

        assertEquals(404, result.response.status)
    }

    @TestConfiguration
    class MockConfig {
        @Bean
        fun attendanceService(): AttendanceService = mock(AttendanceService::class.java)
    }
}

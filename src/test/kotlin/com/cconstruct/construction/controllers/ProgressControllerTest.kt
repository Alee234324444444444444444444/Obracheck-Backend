package com.cconstruct.construction.controllers

import com.cconstruct.construction.constants.Routes
import com.cconstruct.construction.exceptions.ProgressNotFoundException
import com.cconstruct.construction.exceptions.SiteNotFoundException
import com.cconstruct.construction.exceptions.WorkerNotFoundException
import com.cconstruct.construction.models.requests.CreateProgressRequest
import com.cconstruct.construction.models.responses.*
import com.cconstruct.construction.services.ProgressService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import java.time.LocalDateTime
import kotlin.test.assertEquals

@WebMvcTest(ProgressController::class)
@Import(ProgressControllerTest.MockConfig::class)
class ProgressControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var progressService: ProgressService

    private lateinit var objectMapper: ObjectMapper

    private val BASE_URL = Routes.PROGRESSES

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    }

    @Test
    fun should_return_all_progresses() {
        val site = SiteSummaryResponse(1L, "Obra Norte")
        val worker = WorkerSummaryResponse(1L, "Luis", "Albañil")
        val evidences = listOf(EvidenceSummaryResponse(1L, "foto.jpg"))

        val progresses = listOf(
            ProgressResponse(1L, "Avance 1", LocalDateTime.now(), site, worker, evidences),
            ProgressResponse(2L, "Avance 2", LocalDateTime.now(), site, worker, listOf())
        )

        `when`(progressService.listProgresses()).thenReturn(progresses)

        val result = mockMvc.get(BASE_URL)
            .andExpect {
                status { isOk() }
                jsonPath("$[0].description") { value("Avance 1") }
                jsonPath("$[1].description") { value("Avance 2") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_progress_by_id() {
        val site = SiteSummaryResponse(1L, "Obra Norte")
        val worker = WorkerSummaryResponse(1L, "Luis", "Albañil")
        val evidences = listOf(EvidenceSummaryResponse(1L, "foto.jpg"))

        val progress = ProgressResponse(1L, "Avance 1", LocalDateTime.now(), site, worker, evidences)

        `when`(progressService.getProgressById(1L)).thenReturn(progress)

        val result = mockMvc.get("$BASE_URL/1")
            .andExpect {
                status { isOk() }
                jsonPath("$.description") { value("Avance 1") }
                jsonPath("$.site.name") { value("Obra Norte") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_create_progress() {
        val request = CreateProgressRequest("Avance 1", LocalDateTime.now(), 1L, 1L)
        val site = SiteSummaryResponse(1L, "Obra Norte")
        val worker = WorkerSummaryResponse(1L, "Luis", "Albañil")
        val response = ProgressResponse(1L, request.description, request.date, site, worker, listOf())

        `when`(progressService.createProgress(request)).thenReturn(response)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isOk() }
            jsonPath("$.description") { value("Avance 1") }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_update_progress() {
        val request = CreateProgressRequest("Avance Modificado", LocalDateTime.now(), 1L, 1L)
        val site = SiteSummaryResponse(1L, "Obra Norte")
        val worker = WorkerSummaryResponse(1L, "Luis", "Albañil")
        val response = ProgressResponse(1L, request.description, request.date, site, worker, listOf())

        `when`(progressService.updateProgress(1L, request)).thenReturn(response)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.put("$BASE_URL/1") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isOk() }
            jsonPath("$.description") { value("Avance Modificado") }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_delete_progress() {
        val result = mockMvc.delete("$BASE_URL/1")
            .andExpect { status { isNoContent() } }
            .andReturn()

        verify(progressService).deleteProgress(1L)
        assertEquals(204, result.response.status)
    }

    @Test
    fun should_return_404_when_progress_not_found() {
        `when`(progressService.getProgressById(99L))
            .thenThrow(ProgressNotFoundException("No existe"))

        val result = mockMvc.get("$BASE_URL/99")
            .andExpect { status { isNotFound() } }
            .andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_return_404_when_site_not_found_on_create() {
        val request = CreateProgressRequest("Avance", LocalDateTime.now(), 999L, 1L)

        `when`(progressService.createProgress(request))
            .thenThrow(SiteNotFoundException("No existe"))

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isNotFound() }
        }.andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_return_404_when_worker_not_found_on_create() {
        val request = CreateProgressRequest("Avance", LocalDateTime.now(), 1L, 999L)

        `when`(progressService.createProgress(request))
            .thenThrow(WorkerNotFoundException("No existe"))

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isNotFound() }
        }.andReturn()

        assertEquals(404, result.response.status)
    }

    @TestConfiguration
    class MockConfig {
        @Bean
        fun progressService(): ProgressService = mock(ProgressService::class.java)
    }
}

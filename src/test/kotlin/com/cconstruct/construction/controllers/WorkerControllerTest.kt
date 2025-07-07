package com.cconstruct.construction.controllers

import com.cconstruct.construction.constants.Routes
import com.cconstruct.construction.exceptions.SiteNotFoundException
import com.cconstruct.construction.exceptions.WorkerAlreadyExistsException
import com.cconstruct.construction.exceptions.WorkerNotFoundException
import com.cconstruct.construction.models.requests.CreateWorkerRequest
import com.cconstruct.construction.models.responses.SiteSummaryResponse
import com.cconstruct.construction.models.responses.WorkerResponse
import com.cconstruct.construction.services.WorkerService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import kotlin.test.assertEquals

@WebMvcTest(WorkerController::class)
@Import(WorkerControllerTest.MockConfig::class)
class WorkerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var workerService: WorkerService

    private lateinit var objectMapper: ObjectMapper

    private val BASE_URL = Routes.WORKERS

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    }




    @Test
    fun should_create_worker() {
        reset(workerService)
        val request = CreateWorkerRequest("Luis", "Albañil", "1234567890", 1L)
        val site = SiteSummaryResponse(1L, "Obra Norte")
        val response = WorkerResponse(1L, request.name, request.role, request.ci, site)

        `when`(workerService.createWorker(request)).thenReturn(response)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isOk() }
            jsonPath("$.ci") { value("1234567890") }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_all_workers() {
        val site = SiteSummaryResponse(1L, "Obra Norte")

        val workers = listOf(
            WorkerResponse(1L, "Luis", "Albañil", "1234567890", site),
            WorkerResponse(2L, "Carlos", "Soldador", "0987654321", site)
        )

        `when`(workerService.listWorkers()).thenReturn(workers)

        val result = mockMvc.get(BASE_URL)
            .andExpect {
                status { isOk() }
                jsonPath("$[0].name") { value("Luis") }
                jsonPath("$[1].role") { value("Soldador") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_worker_by_id() {
        val site = SiteSummaryResponse(1L, "Obra Norte")
        val worker = WorkerResponse(1L, "Luis", "Albañil", "1234567890", site)

        `when`(workerService.getWorkerById(1L)).thenReturn(worker)

        val result = mockMvc.get("$BASE_URL/1")
            .andExpect {
                status { isOk() }
                jsonPath("$.name") { value("Luis") }
                jsonPath("$.role") { value("Albañil") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }



    @Test
    fun should_update_worker() {
        val request = CreateWorkerRequest("Luis M.", "Supervisor", "1234567890", 1L)
        val site = SiteSummaryResponse(1L, "Obra Norte")
        val response = WorkerResponse(1L, request.name, request.role, request.ci, site)

        `when`(workerService.updateWorker(1L, request)).thenReturn(response)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.put("$BASE_URL/1") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isOk() }
            jsonPath("$.role") { value("Supervisor") }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_delete_worker() {
        val result = mockMvc.delete("$BASE_URL/1")
            .andExpect { status { isNoContent() } }
            .andReturn()

        verify(workerService).deleteWorker(1L)
        assertEquals(204, result.response.status)
    }

    @Test
    fun should_return_409_when_worker_already_exists() {
        val request = CreateWorkerRequest("Luis", "Albañil", "1234567890", 1L)

        `when`(workerService.createWorker(request))
            .thenThrow(WorkerAlreadyExistsException("Ya existe"))

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isConflict() }
        }.andReturn()

        assertEquals(409, result.response.status)
    }

    @Test
    fun should_return_404_when_worker_not_found() {
        `when`(workerService.getWorkerById(99L))
            .thenThrow(WorkerNotFoundException("No existe"))

        val result = mockMvc.get("$BASE_URL/99")
            .andExpect { status { isNotFound() } }
            .andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_return_404_when_site_not_found_on_create() {
        val request = CreateWorkerRequest("Luis", "Albañil", "1234567890", 99L)

        `when`(workerService.createWorker(request))
            .thenThrow(SiteNotFoundException("Sitio no encontrado"))

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
        fun workerService(): WorkerService = mock(WorkerService::class.java)
    }
}

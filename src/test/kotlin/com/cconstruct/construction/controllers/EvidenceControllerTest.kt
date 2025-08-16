package com.cconstruct.construction.controllers

import com.cconstruct.construction.constants.Routes
import com.cconstruct.construction.exceptions.EvidenceAlreadyExistsException
import com.cconstruct.construction.exceptions.EvidenceNotFoundException
import com.cconstruct.construction.exceptions.ProgressNotFoundException
import com.cconstruct.construction.models.dtos.EvidenceDto
import com.cconstruct.construction.models.responses.EvidenceListResponse
import com.cconstruct.construction.models.entities.Evidence

import com.cconstruct.construction.models.responses.EvidenceResponse
import com.cconstruct.construction.models.responses.EvidenceUploadResponse
import com.cconstruct.construction.services.EvidenceService
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
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime
import kotlin.test.assertEquals

@WebMvcTest(EvidenceController::class)
@Import(EvidenceControllerTest.MockConfig::class)
class EvidenceControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var evidenceService: EvidenceService

    private lateinit var objectMapper: ObjectMapper

    private val BASE_URL = Routes.EVIDENCES

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    }

    @Test
    fun should_upload_evidence() {
        val now = LocalDateTime.now()
        val file = MockMultipartFile("file_name", "photo.jpg", "image/jpeg", byteArrayOf(1, 2, 3))
        val dto = EvidenceDto(1L, "photo.jpg", "photo.jpg", "image/jpeg", 3, now)
        val response = EvidenceUploadResponse("Image uploaded successfully.", dto)

        `when`(evidenceService.uploadEvidence(file, 1L)).thenReturn(response)

        val result = mockMvc.perform(multipart("$BASE_URL/upload")
            .file(file)
            .param("progress_id", "1")
            .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Image uploaded successfully."))
            .andExpect(jsonPath("$.image.file_name").value("photo.jpg"))
            .andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_404_when_progress_not_found_on_upload() {
        val file = MockMultipartFile("file_name", "photo.jpg", "image/jpeg", byteArrayOf(1, 2, 3))

        `when`(evidenceService.uploadEvidence(file, 99L))
            .thenThrow(ProgressNotFoundException("Progress not found"))

        val result = mockMvc.perform(multipart("$BASE_URL/upload")
            .file(file)
            .param("progress_id", "99")
            .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isNotFound())
            .andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_update_evidence() {
        val now = LocalDateTime.now()
        val file = MockMultipartFile("file_name", "updated.jpg", "image/jpeg", byteArrayOf(10, 20))
        val dto = EvidenceDto(1L, "updated.jpg", "updated.jpg", "image/jpeg", 2, now)
        val response = EvidenceUploadResponse("Image updated successfully.", dto)

        `when`(evidenceService.updateEvidence(1L, file)).thenReturn(response)

        val result = mockMvc.perform(multipart("$BASE_URL/1")
            .file(file)
            .with { it.method = "PUT"; it }
            .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Image updated successfully."))
            .andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_404_when_evidence_not_found_on_update() {
        val file = MockMultipartFile("file_name", "updated.jpg", "image/jpeg", byteArrayOf(1))

        `when`(evidenceService.updateEvidence(999L, file))
            .thenThrow(EvidenceNotFoundException("Not found"))

        val result = mockMvc.perform(multipart("$BASE_URL/999")
            .file(file)
            .with { it.method = "PUT"; it }
            .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isNotFound())
            .andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_return_all_evidences() {
        val now = LocalDateTime.now()
        val list = EvidenceListResponse(
            images = listOf(EvidenceDto(1L, "a.jpg", "a.jpg", "image/jpeg", 123, now)),
            total = 1
        )

        `when`(evidenceService.listEvidences()).thenReturn(list)

        val result = mockMvc.perform(get(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(1))
            .andExpect(jsonPath("$.images[0].file_name").value("a.jpg"))
            .andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_get_evidence_by_id() {
        val response = EvidenceResponse(1L, "foto.jpg", 1L)

        `when`(evidenceService.getEvidenceById(1L)).thenReturn(response)

        val result = mockMvc.perform(get("$BASE_URL/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.file_name").value("foto.jpg"))
            .andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_404_when_getting_nonexistent_evidence() {
        `when`(evidenceService.getEvidenceById(999L))
            .thenThrow(EvidenceNotFoundException("Not found"))

        val result = mockMvc.perform(get("$BASE_URL/999")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_delete_evidence() {
        val result = mockMvc.perform(delete("$BASE_URL/1"))
            .andExpect(status().isNoContent())
            .andReturn()

        verify(evidenceService).deleteEvidence(1L)
        assertEquals(204, result.response.status)
    }


    @Test
    fun should_return_409_when_evidence_already_exists_on_upload() {
        val file = MockMultipartFile("file_name", "duplicada.jpg", "image/jpeg", byteArrayOf(1, 2, 3))

        `when`(evidenceService.uploadEvidence(file, 1L))
            .thenThrow(EvidenceAlreadyExistsException("An evidence with the name already exists."))

        val result = mockMvc.perform(multipart("$BASE_URL/upload")
            .file(file)
            .param("progress_id", "1")
            .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isConflict())
            .andReturn()

        assertEquals(409, result.response.status)
    }


    @Test
    fun should_download_evidence() {
        val fileBytes = byteArrayOf(1, 2, 3)

        val evidence = mock<Evidence>()
        `when`(evidence.contentType).thenReturn("image/jpeg")
        `when`(evidence.originalFileName).thenReturn("test.jpg")

        `when`(evidenceService.downloadEvidence(1L)).thenReturn(Pair(evidence, fileBytes))

        val result = mockMvc.perform(get("$BASE_URL/1/download"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "image/jpeg"))
            .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"test.jpg\""))
            .andReturn()

        assertEquals(200, result.response.status)
        assertEquals(fileBytes.size, result.response.contentAsByteArray.size)
    }



    @TestConfiguration
    class MockConfig {
        @Bean
        fun evidenceService(): EvidenceService = mock(EvidenceService::class.java)
    }
}

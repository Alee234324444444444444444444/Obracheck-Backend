package com.cconstruct.construction.controllers

import com.cconstruct.construction.constants.Routes
import com.cconstruct.construction.exceptions.SiteAlreadyExistsException
import com.cconstruct.construction.exceptions.SiteNotFoundException
import com.cconstruct.construction.exceptions.UserNotFoundException
import com.cconstruct.construction.models.requests.CreateSiteRequest
import com.cconstruct.construction.models.responses.SiteResponse
import com.cconstruct.construction.models.responses.UserResponse
import com.cconstruct.construction.services.SiteService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
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

@WebMvcTest(SiteController::class)
@Import(SiteControllerTest.MockConfig::class)
class SiteControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var siteService: SiteService

    private lateinit var objectMapper: ObjectMapper

    private val BASE_URL = Routes.SITES

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    }

    @Test
    fun should_return_all_sites() {
        val user = UserResponse(1L, "Alejandro", "alejandro@puce.edu.ec")

        val sites = listOf(
            SiteResponse(1L, "Obra Norte", "Quito", listOf(), listOf(), user),
            SiteResponse(2L, "Obra Sur", "Guayaquil", listOf(), listOf(), user)
        )

        `when`(siteService.listSites()).thenReturn(sites)

        val result = mockMvc.get(BASE_URL)
            .andExpect {
                status { isOk() }
                jsonPath("$[0].name") { value("Obra Norte") }
                jsonPath("$[1].address") { value("Guayaquil") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_site_by_id() {
        val user = UserResponse(1L, "Alejandro", "alejandro@puce.edu.ec")
        val site = SiteResponse(1L, "Obra Norte", "Quito", listOf(), listOf(), user)

        `when`(siteService.getSiteById(1L)).thenReturn(site)

        val result = mockMvc.get("$BASE_URL/1")
            .andExpect {
                status { isOk() }
                jsonPath("$.name") { value("Obra Norte") }
                jsonPath("$.address") { value("Quito") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_create_site() {
        val request = CreateSiteRequest("Obra Centro", "Ambato", 1L)
        val user = UserResponse(1L, "Alejandro", "alejandro@puce.edu.ec")
        val response = SiteResponse(1L, request.name, request.address, listOf(), listOf(), user)

        `when`(siteService.createSite(request)).thenReturn(response)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isOk() }
            jsonPath("$.name") { value("Obra Centro") }
            jsonPath("$.address") { value("Ambato") }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_update_site() {
        val request = CreateSiteRequest("Obra Norte Modificada", "Quito Centro", 1L)
        val user = UserResponse(1L, "Alejandro", "alejandro@puce.edu.ec")
        val response = SiteResponse(1L, request.name, request.address, listOf(), listOf(), user)

        `when`(siteService.updateSite(1L, request)).thenReturn(response)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.put("$BASE_URL/1") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isOk() }
            jsonPath("$.address") { value("Quito Centro") }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_delete_site() {
        val result = mockMvc.delete("$BASE_URL/1")
            .andExpect { status { isNoContent() } }
            .andReturn()

        verify(siteService).deleteSite(1L)
        assertEquals(204, result.response.status)
    }

    @Test
    fun should_return_409_when_creating_existing_site() {
        val request = CreateSiteRequest("Obra Norte", "Quito", 1L)

        `when`(siteService.createSite(request)).thenThrow(SiteAlreadyExistsException("Ya existe"))

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
    fun should_return_404_when_getting_nonexistent_site() {
        `when`(siteService.getSiteById(99L))
            .thenThrow(SiteNotFoundException("No existe"))

        val result = mockMvc.get("$BASE_URL/99")
            .andExpect { status { isNotFound() } }
            .andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_return_404_when_user_not_found_on_create() {
        val request = CreateSiteRequest("Obra Este", "Tena", 999L)

        `when`(siteService.createSite(request))
            .thenThrow(UserNotFoundException("User not found"))

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect { status { isNotFound() } }
            .andReturn()

        assertEquals(404, result.response.status)
    }

    @TestConfiguration
    class MockConfig {
        @Bean
        fun siteService(): SiteService = mock(SiteService::class.java)
    }
}

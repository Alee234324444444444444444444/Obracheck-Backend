package com.cconstruct.construction.controllers

import com.cconstruct.construction.constants.Routes
import com.cconstruct.construction.exceptions.UserAlreadyExistsException
import com.cconstruct.construction.exceptions.UserNotFoundException
import com.cconstruct.construction.models.requests.CreateUserRequest
import com.cconstruct.construction.models.responses.UserResponse
import com.cconstruct.construction.services.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
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

@WebMvcTest(UserController::class)
@Import(UserControllerTest.MockConfig::class)
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userService: UserService

    private lateinit var objectMapper: ObjectMapper

    private val BASE_URL = Routes.USERS

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    }

    @Test
    fun should_return_all_users() {
        val users = listOf(
            UserResponse(1L, "Alex", "alex@puce.edu.ec"),
            UserResponse(2L, "Paul", "paul@puce.edu.ec")
        )

        `when`(userService.listUsers()).thenReturn(users)

        val result = mockMvc.get(BASE_URL)
            .andExpect {
                status { isOk() }
                jsonPath("$[0].name") { value("Alex") }
                jsonPath("$[1].email") { value("paul@puce.edu.ec") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_return_user_by_id() {
        val user = UserResponse(1L, "Alejandro", "apbarrionuevo@puce.edu.ec")

        `when`(userService.getUserById(1L)).thenReturn(user)

        val result = mockMvc.get("$BASE_URL/1")
            .andExpect {
                status { isOk() }
                jsonPath("$.name") { value("Alejandro") }
                jsonPath("$.email") { value("apbarrionuevo@puce.edu.ec") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_create_user() {
        val request = CreateUserRequest("Alejandro", "apbarrionuevo@puce.edu.ec")
        val response = UserResponse(1L, request.name, request.email)

        `when`(userService.createUser(request)).thenReturn(response)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isOk() }
            jsonPath("$.name") { value("Alejandro") }
            jsonPath("$.email") { value("apbarrionuevo@puce.edu.ec") }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_update_user() {
        val request = CreateUserRequest("Alejandro", "apbarrionuevo@puce.edu.ec")
        val response = UserResponse(1L, request.name, request.email)

        `when`(userService.updateUser(1L, request)).thenReturn(response)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.put("$BASE_URL/1") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isOk() }
            jsonPath("$.name") { value("Alejandro") }
            jsonPath("$.email") { value("apbarrionuevo@puce.edu.ec") }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_delete_user() {
        val result = mockMvc.delete("$BASE_URL/1")
            .andExpect {
                status { isNoContent() }
            }.andReturn()

        verify(userService).deleteUser(1L)
        assertEquals(204, result.response.status)
    }

    @Test
    fun should_return_409_when_creating_user_with_existing_email() {
        val request = CreateUserRequest("Alejandro", "apbarrionuevo@puce.edu.ec")

        `when`(userService.createUser(request))
            .thenThrow(UserAlreadyExistsException("User with email ${request.email} already exists."))

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isConflict() } // 409
        }.andReturn()

        assertEquals(409, result.response.status)
    }

    @Test
    fun should_return_404_when_getting_nonexistent_user() {
        `when`(userService.getUserById(999L))
            .thenThrow(UserNotFoundException("User with ID 999 not found."))

        val result = mockMvc.get("$BASE_URL/999")
            .andExpect {
                status { isNotFound() }
            }.andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_return_404_when_updating_nonexistent_user() {
        val request = CreateUserRequest("Nuevo", "nuevo@puce.edu.ec")

        `when`(userService.updateUser(999L, request))
            .thenThrow(UserNotFoundException("User with ID 999 not found."))

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.put("$BASE_URL/999") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isNotFound() }
        }.andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun should_return_409_when_updating_with_existing_email() {
        val request = CreateUserRequest("Nuevo", "repetido@puce.edu.ec")

        `when`(userService.updateUser(1L, request))
            .thenThrow(UserAlreadyExistsException("User with email ${request.email} already exists."))

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.put("$BASE_URL/1") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isConflict() }
        }.andReturn()

        assertEquals(409, result.response.status)
    }

    @Test
    fun should_return_404_when_deleting_nonexistent_user() {
        doThrow(UserNotFoundException("User with ID 888 not found."))
            .`when`(userService).deleteUser(888L)

        val result = mockMvc.delete("$BASE_URL/888")
            .andExpect {
                status { isNotFound() }
            }.andReturn()

        assertEquals(404, result.response.status)
    }

    @TestConfiguration
    class MockConfig {
        @Bean
        fun userService(): UserService = mock(UserService::class.java)
    }
}

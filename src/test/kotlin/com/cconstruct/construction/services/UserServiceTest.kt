package com.cconstruct.construction.services

import com.cconstruct.construction.exceptions.UserAlreadyExistsException
import com.cconstruct.construction.exceptions.UserNotFoundException
import com.cconstruct.construction.mappers.UserMapper
import com.cconstruct.construction.models.entities.User
import com.cconstruct.construction.models.requests.CreateUserRequest
import com.cconstruct.construction.models.responses.UserResponse
import com.cconstruct.construction.repositories.UserRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var userMapper: UserMapper
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        userMapper = mock(UserMapper::class.java)
        userService = UserService(userRepository, userMapper)
    }

    @Test
    fun `should create a new user`() {
        val request = CreateUserRequest("Alejandro", "alejandro@puce.edu.ec")
        val user = User(name = request.name, email = request.email).apply { id = 1L }
        val response = UserResponse(1L, "Alejandro", "alejandro@puce.edu.ec")

        `when`(userRepository.existsByEmail(request.email)).thenReturn(false)
        `when`(userRepository.save(any(User::class.java))).thenReturn(user)
        `when`(userMapper.toResponse(user)).thenReturn(response)

        val result = userService.createUser(request)

        assertEquals("Alejandro", result.name)
        assertEquals("alejandro@puce.edu.ec", result.email)
    }

    @Test
    fun `should throw exception when creating user with existing email`() {
        val request = CreateUserRequest("Alejandro", "alejandro@puce.edu.ec")

        `when`(userRepository.existsByEmail(request.email)).thenReturn(true)

        assertThrows<UserAlreadyExistsException> {
            userService.createUser(request)
        }
    }

    @Test
    fun `should return all users`() {
        val user = User(name = "Alejandro", email = "alejandro@puce.edu.ec").apply { id = 1L }
        val response = UserResponse(1L, "Alejandro", "alejandro@puce.edu.ec")

        `when`(userRepository.findAll()).thenReturn(listOf(user))
        `when`(userMapper.toResponseList(listOf(user))).thenReturn(listOf(response))

        val result = userService.listUsers()

        assertEquals(1, result.size)
        assertEquals("Alejandro", result[0].name)
    }

    @Test
    fun `should return user by id`() {
        val user = User(name = "Alejandro", email = "alejandro@puce.edu.ec").apply { id = 1L }
        val response = UserResponse(1L, "Alejandro", "alejandro@puce.edu.ec")

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
        `when`(userMapper.toResponse(user)).thenReturn(response)

        val result = userService.getUserById(1L)

        assertEquals("Alejandro", result.name)
    }

    @Test
    fun `should throw exception when user by id not found`() {
        `when`(userRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<UserNotFoundException> {
            userService.getUserById(1L)
        }
    }

    @Test
    fun `should update user when email is changed and not used`() {
        val existingUser = User(name = "Old", email = "old@puce.edu.ec").apply { id = 1L }
        val request = CreateUserRequest("New", "new@puce.edu.ec")
        val updatedUser = User(name = "New", email = "new@puce.edu.ec").apply { id = 1L }
        val response = UserResponse(1L, "New", "new@puce.edu.ec")

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(existingUser))
        `when`(userRepository.existsByEmail(request.email)).thenReturn(false)
        `when`(userRepository.save(existingUser)).thenReturn(updatedUser)
        `when`(userMapper.toResponse(updatedUser)).thenReturn(response)

        val result = userService.updateUser(1L, request)

        assertEquals("New", result.name)
        assertEquals("new@puce.edu.ec", result.email)
    }

    @Test
    fun `should update user when email is not changed`() {
        val existingUser = User(name = "Old", email = "same@puce.edu.ec").apply { id = 1L }
        val request = CreateUserRequest("Updated", "same@puce.edu.ec")
        val updatedUser = User(name = "Updated", email = "same@puce.edu.ec").apply { id = 1L }
        val response = UserResponse(1L, "Updated", "same@puce.edu.ec")

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(existingUser))
        `when`(userRepository.save(existingUser)).thenReturn(updatedUser)
        `when`(userMapper.toResponse(updatedUser)).thenReturn(response)

        val result = userService.updateUser(1L, request)

        assertEquals("Updated", result.name)
        assertEquals("same@puce.edu.ec", result.email)
    }

    @Test
    fun `should throw exception when updating with existing email`() {
        val existingUser = User(name = "Old", email = "old@puce.edu.ec").apply { id = 1L }
        val request = CreateUserRequest("New", "used@puce.edu.ec")

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(existingUser))
        `when`(userRepository.existsByEmail(request.email)).thenReturn(true)

        assertThrows<UserAlreadyExistsException> {
            userService.updateUser(1L, request)
        }
    }

    @Test
    fun `should delete user`() {
        val user = User(name = "Alejandro", email = "alejandro@puce.edu.ec").apply { id = 1L }

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))

        userService.deleteUser(1L)

        verify(userRepository).delete(user)
    }

    @Test
    fun `should throw exception when deleting non-existent user`() {
        `when`(userRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<UserNotFoundException> {
            userService.deleteUser(1L)
        }
    }


}

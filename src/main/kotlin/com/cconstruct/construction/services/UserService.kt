package com.cconstruct.construction.services

import com.cconstruct.construction.exceptions.UserAlreadyExistsException
import com.cconstruct.construction.exceptions.UserNotFoundException
import com.cconstruct.construction.mappers.UserMapper
import com.cconstruct.construction.models.entities.User
import com.cconstruct.construction.models.requests.CreateUserRequest
import com.cconstruct.construction.models.responses.UserResponse
import com.cconstruct.construction.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) {

    fun createUser(request: CreateUserRequest): UserResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw UserAlreadyExistsException("User with email ${request.email} already exists.")
        }

        val user = User(
            name = request.name,
            email = request.email
        )
        return userMapper.toResponse(userRepository.save(user))
    }

    fun getUserById(id: Long): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User with ID $id not found.") }
        return userMapper.toResponse(user)
    }

    fun listUsers(): List<UserResponse> =
        userMapper.toResponseList(userRepository.findAll())

    fun deleteUser(id: Long) {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User with ID $id not found.") }
        userRepository.delete(user)
    }

    fun updateUser(id: Long, request: CreateUserRequest): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User with ID $id not found.") }

        if (user.email != request.email && userRepository.existsByEmail(request.email)) {
            throw UserAlreadyExistsException("User with email ${request.email} already exists.")
        }


        user.name = request.name
        user.email = request.email

        return userMapper.toResponse(userRepository.save(user))
    }
}
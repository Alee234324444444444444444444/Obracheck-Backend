package com.cconstruct.construction.mappers

import com.cconstruct.construction.models.entities.User
import com.cconstruct.construction.models.requests.CreateUserRequest
import com.cconstruct.construction.models.responses.UserResponse
import org.springframework.stereotype.Component

@Component
class UserMapper : BaseMapper<User, UserResponse> {
    override fun toResponse(entity: User): UserResponse {
        return UserResponse(
            id = entity.id,
            name = entity.name,
            email = entity.email
        )
    }

    fun toEntity(request: CreateUserRequest): User {
        return User(
            name = request.name,
            email = request.email
        )
    }
}
package com.cconstruct.construction.models.requests

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateUserRequest(
    @JsonProperty("name")
    val name: String,

    @JsonProperty("email")
    val email: String,


    @JsonProperty("password")
    val password: String
)
package com.cconstruct.construction.models.requests

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateSiteRequest(
    val name: String,
    val address: String,


    @JsonProperty("user_id")
    val userId: Long
)
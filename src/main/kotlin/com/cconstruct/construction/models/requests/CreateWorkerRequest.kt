package com.cconstruct.construction.models.requests

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateWorkerRequest(
    val name: String,
    val role: String,
    val  ci: String,

    @JsonProperty("site_id")
    val siteId: Long
)

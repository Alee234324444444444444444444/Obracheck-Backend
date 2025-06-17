package com.cconstruct.construction.models.requests

import com.fasterxml.jackson.annotation.JsonProperty

data class UploadEvidenceRequest(
    @JsonProperty("file_name")
    val fileName: String,

    //val content: String,

    @JsonProperty("progress_id")
    val progressId: Long
)
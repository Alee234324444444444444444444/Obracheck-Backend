package com.cconstruct.construction.models.responses

import com.cconstruct.construction.models.dtos.EvidenceDto

data class EvidenceUploadResponse(
    val message: String,
    val image: EvidenceDto?
)
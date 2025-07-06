package com.cconstruct.construction.models.responses

import com.cconstruct.construction.models.dtos.EvidenceDto

data class EvidenceListResponse(
    val images: List<EvidenceDto>,
    val total: Long
)
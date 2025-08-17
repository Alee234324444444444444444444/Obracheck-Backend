package com.cconstruct.construction.models.dtos

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class EvidenceDto(
    val id: Long,
    val fileName: String,
    val originalFileName: String,
    val contentType: String,
    val fileSize: Long,
    val uploadDate: LocalDateTime,
    val progressId: Long
)
package com.cconstruct.construction.models.dtos

import java.time.LocalDateTime

data class EvidenceDto(
    val id: Long,
    val fileName: String,
    val originalFileName: String,
    val contentType: String,
    val fileSize: Long,
    val uploadDate: LocalDateTime
)
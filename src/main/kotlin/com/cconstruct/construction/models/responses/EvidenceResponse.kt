package com.cconstruct.construction.models.responses

data class EvidenceResponse(
    val id: Long,
    val fileName: String,
    val progressId: Long,
    val contentBase64: String // puedes convertir ByteArray a base64
)
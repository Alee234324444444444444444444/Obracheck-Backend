package com.cconstruct.construction.mappers

import com.cconstruct.construction.models.dtos.EvidenceDto
import com.cconstruct.construction.models.entities.Evidence
import com.cconstruct.construction.models.responses.EvidenceResponse
import org.springframework.stereotype.Component

@Component
class EvidenceMapper : BaseMapper<Evidence, EvidenceResponse> {

    override fun toResponse(entity: Evidence): EvidenceResponse {
        return EvidenceResponse(
            id = entity.id,
            fileName = entity.fileName,
            progressId = entity.progress.id
        )
    }


    fun toDto(entity: Evidence): EvidenceDto {
        return EvidenceDto(
            id = entity.id,
            fileName = entity.fileName,
            originalFileName = entity.originalFileName,
            contentType = entity.contentType,
            fileSize = entity.fileSize,
            uploadDate = entity.uploadDate
        )
    }

    fun toDtoList(entities: List<Evidence>): List<EvidenceDto> = entities.map { toDto(it) }

}
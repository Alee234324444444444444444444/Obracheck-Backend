package com.cconstruct.construction.mappers

import com.cconstruct.construction.models.entities.Evidence
import com.cconstruct.construction.models.entities.Progress
import com.cconstruct.construction.models.requests.UploadEvidenceRequest
import com.cconstruct.construction.models.responses.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class EvidenceMapper : BaseMapper<Evidence, EvidenceResponse> {
    override fun toResponse(entity: Evidence): EvidenceResponse {
        return EvidenceResponse(
            id = entity.id,
            fileName = entity.fileName,
            progressId = entity.progress.id,
            //contentBase64 = Base64.getEncoder().encodeToString(entity.content)
        )
    }

    fun toEntity(request: UploadEvidenceRequest, progress: Progress): Evidence {
        return Evidence(
            fileName = request.fileName,
            progress = progress
        )
    }
}

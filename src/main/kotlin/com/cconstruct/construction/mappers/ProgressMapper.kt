package com.cconstruct.construction.mappers

import com.cconstruct.construction.models.entities.Progress
import com.cconstruct.construction.models.entities.Site
import com.cconstruct.construction.models.entities.Worker
import com.cconstruct.construction.models.requests.CreateProgressRequest
import com.cconstruct.construction.models.responses.*
import org.springframework.stereotype.Component

@Component
class ProgressMapper : BaseMapper<Progress, ProgressResponse> {
    override fun toResponse(entity: Progress): ProgressResponse {
        return ProgressResponse(
            id = entity.id,
            description = entity.description,
            date = entity.date,
            site = SiteSummaryResponse(
                id = entity.site.id,
                name = entity.site.name
            ),
            worker = WorkerSummaryResponse(
                id = entity.worker.id,
                name = entity.worker.name,
                role = entity.worker.role
            ),
            evidences = entity.evidences.map { evidence ->
                EvidenceSummaryResponse(
                    id = evidence.id,
                    fileName = evidence.fileName
                )
            }
        )
    }

    fun toEntity(request: CreateProgressRequest, site: Site, worker: Worker): Progress {
        return Progress(
            description = request.description,
            date = request.date,
            site = site,
            worker = worker
        )
    }
}

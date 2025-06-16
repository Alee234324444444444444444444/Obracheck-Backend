package com.cconstruct.construction.mappers

import com.cconstruct.construction.models.entities.Worker
import com.cconstruct.construction.models.responses.*
import org.springframework.stereotype.Component

@Component
class WorkerMapper : BaseMapper<Worker, WorkerResponse> {
    override fun toResponse(entity: Worker): WorkerResponse {
        return WorkerResponse(
            id = entity.id,
            name = entity.name,
            role = entity.role,
            ci = entity.ci,
            site = SiteSummaryResponse(
                id = entity.site.id,
                name = entity.site.name
            )
        )
    }
}
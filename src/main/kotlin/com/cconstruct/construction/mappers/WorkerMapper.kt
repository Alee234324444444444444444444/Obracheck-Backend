package com.cconstruct.construction.mappers

import com.cconstruct.construction.models.entities.Site
import com.cconstruct.construction.models.entities.Worker
import com.cconstruct.construction.models.requests.CreateWorkerRequest
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

    fun toEntity(request: CreateWorkerRequest, site: Site): Worker {
        return Worker(
            name = request.name,
            role = request.role,
            ci = request.ci,
            site = site
        )
    }
}
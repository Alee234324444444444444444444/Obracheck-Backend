package com.cconstruct.construction.mappers

import com.cconstruct.construction.models.responses.ProgressSummaryResponse
import com.cconstruct.construction.models.responses.SiteResponse
import com.cconstruct.construction.models.responses.WorkerSummaryResponse
import com.cconstruct.construction.models.entities.Site
import com.cconstruct.construction.models.responses.*
import org.springframework.stereotype.Component

@Component
class SiteMapper : BaseMapper<Site, SiteResponse> {
    override fun toResponse(entity: Site): SiteResponse {
        return SiteResponse(
            id = entity.id,
            name = entity.name,
            address = entity.address,
            workers = entity.workers.map { worker ->
                WorkerSummaryResponse(
                    id = worker.id,
                    name = worker.name,
                    role = worker.role
                )
            },
            progresses = entity.progresses.map { progress ->
                ProgressSummaryResponse(
                    id = progress.id,
                    description = progress.description,
                    date = progress.date.toString()
                )
            },
            user = UserResponse(
                id = entity.user.id,
                name = entity.user.name,
                email = entity.user.email

            )
        )
    }
}
package com.cconstruct.construction.services

import com.cconstruct.construction.exceptions.ProgressNotFoundException
import com.cconstruct.construction.exceptions.SiteNotFoundException
import com.cconstruct.construction.exceptions.WorkerNotFoundException
import com.cconstruct.construction.mappers.ProgressMapper
import com.cconstruct.construction.models.entities.Progress
import com.cconstruct.construction.models.requests.CreateProgressRequest
import com.cconstruct.construction.models.responses.ProgressResponse
import com.cconstruct.construction.repositories.ProgressRepository
import com.cconstruct.construction.repositories.SiteRepository
import com.cconstruct.construction.repositories.WorkerRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProgressService(
    private val progressRepository: ProgressRepository,
    private val siteRepository: SiteRepository,
    private val workerRepository: WorkerRepository,
    private val progressMapper: ProgressMapper
) {

    fun createProgress(request: CreateProgressRequest): ProgressResponse {
        val site = siteRepository.findById(request.siteId)
            .orElseThrow { SiteNotFoundException("Site with ID ${request.siteId} not found.") }

        val worker = workerRepository.findById(request.workerId)
            .orElseThrow { WorkerNotFoundException("Worker with ID ${request.workerId} not found.") }

        val progress = Progress(
            description = request.description,
            date = LocalDateTime.now(),
            site = site,
            worker = worker
        )
        return progressMapper.toResponse(progressRepository.save(progress))
    }

    fun updateProgress(id: Long, request: CreateProgressRequest): ProgressResponse {
        val progress = progressRepository.findById(id)
            .orElseThrow { ProgressNotFoundException("Progress with ID $id not found.") }

        val site = siteRepository.findById(request.siteId)
            .orElseThrow { SiteNotFoundException("Site with ID ${request.siteId} not found.") }

        val worker = workerRepository.findById(request.workerId)
            .orElseThrow { WorkerNotFoundException("Worker with ID ${request.workerId} not found.") }

        progress.description = request.description
        progress.site = site
        progress.worker = worker

        return progressMapper.toResponse(progressRepository.save(progress))
    }


    fun getProgressById(id: Long): ProgressResponse {
        val progress = progressRepository.findById(id)
            .orElseThrow { ProgressNotFoundException("Progress with ID $id not found.") }
        return progressMapper.toResponse(progress)
    }

    fun listProgresses(): List<ProgressResponse> {
        return progressMapper.toResponseList(progressRepository.findAll())
    }

    fun deleteProgress(id: Long) {
        val progress = progressRepository.findById(id)
            .orElseThrow { ProgressNotFoundException("Progress with ID $id not found.") }
        progressRepository.delete(progress)
    }
}

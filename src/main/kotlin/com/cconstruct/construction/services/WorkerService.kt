package com.cconstruct.construction.services

import com.cconstruct.construction.exceptions.SiteNotFoundException
import com.cconstruct.construction.exceptions.WorkerAlreadyExistsException
import com.cconstruct.construction.exceptions.WorkerNotFoundException
import com.cconstruct.construction.mappers.WorkerMapper
import com.cconstruct.construction.models.entities.Worker
import com.cconstruct.construction.models.requests.CreateWorkerRequest
import com.cconstruct.construction.models.responses.WorkerResponse
import com.cconstruct.construction.repositories.SiteRepository
import com.cconstruct.construction.repositories.WorkerRepository
import org.springframework.stereotype.Service



@Service
class WorkerService(
    private val workerRepository: WorkerRepository,
    private val siteRepository: SiteRepository,
    private val workerMapper: WorkerMapper
) {
    fun createWorker(request: CreateWorkerRequest): WorkerResponse {
        if (workerRepository.existsByCi(request.ci)) {
            throw WorkerAlreadyExistsException("Worker with CI ${request.ci} already exists.") as Throwable
        }
        val site = siteRepository.findById(request.siteId)
            .orElseThrow { SiteNotFoundException("Site with ID ${request.siteId} not found.") }

        val worker = Worker(
            name = request.name,
            role = request.role,
            ci = request.ci,
            site = site
        )
        return workerMapper.toResponse(workerRepository.save(worker))
    }

    fun getWorkerById(id: Long): WorkerResponse {
        val worker = workerRepository.findById(id)
            .orElseThrow { WorkerNotFoundException("Worker with ID $id not found.") }
        return workerMapper.toResponse(worker)
    }

    fun listWorkers(): List<WorkerResponse> =
        workerMapper.toResponseList(workerRepository.findAll())

    fun updateWorker(id: Long, request: CreateWorkerRequest): WorkerResponse {val worker = workerRepository.findById(id)
        .orElseThrow { WorkerNotFoundException("Worker with ID $id not found.") }

        val site = siteRepository.findById(request.siteId)
            .orElseThrow { SiteNotFoundException("Site with ID ${request.siteId} not found.") }

        worker.name = request.name
        worker.role = request.role
        worker.site = site

        return workerMapper.toResponse(workerRepository.save(worker))
    }

    fun deleteWorker(id: Long) {
        val worker = workerRepository.findById(id)
            .orElseThrow { WorkerNotFoundException("Worker with ID $id not found.") }
        workerRepository.delete(worker)
    }
}


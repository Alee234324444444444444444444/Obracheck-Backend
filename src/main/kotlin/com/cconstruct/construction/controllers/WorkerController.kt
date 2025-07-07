package com.cconstruct.construction.controllers

import com.cconstruct.construction.constants.Routes
import com.cconstruct.construction.models.requests.CreateWorkerRequest
import com.cconstruct.construction.models.responses.WorkerResponse
import com.cconstruct.construction.services.WorkerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Routes.WORKERS)
class WorkerController(
    private val workerService: WorkerService
) {

    @PostMapping
    fun createWorker(@RequestBody request: CreateWorkerRequest): WorkerResponse =
        workerService.createWorker(request)

    @GetMapping("/{id}")
    fun getWorkerById(@PathVariable id: Long): WorkerResponse =
        workerService.getWorkerById(id)

    @GetMapping
    fun listWorkers(): List<WorkerResponse> =
        workerService.listWorkers()

    @PutMapping("/{id}")
    fun updateWorker(@PathVariable id: Long, @RequestBody request: CreateWorkerRequest): WorkerResponse =
        workerService.updateWorker(id, request)

    @DeleteMapping("/{id}")
    fun deleteWorker(@PathVariable id: Long): ResponseEntity<Void> {
        workerService.deleteWorker(id)
        return ResponseEntity.noContent().build()
    }

}

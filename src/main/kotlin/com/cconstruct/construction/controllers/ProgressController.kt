package com.cconstruct.construction.controllers

import com.cconstruct.construction.constants.Routes
import com.cconstruct.construction.models.requests.CreateProgressRequest
import com.cconstruct.construction.models.responses.ProgressResponse
import com.cconstruct.construction.services.ProgressService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Routes.PROGRESSES)
class ProgressController(
    private val progressService: ProgressService
) {

    @PostMapping
    fun createProgress(@RequestBody request: CreateProgressRequest): ProgressResponse =
        progressService.createProgress(request)

    @PutMapping("/{id}")
    fun updateProgress(@PathVariable id: Long, @RequestBody request: CreateProgressRequest): ProgressResponse =
        progressService.updateProgress(id, request)

    @GetMapping("/{id}")
    fun getProgressById(@PathVariable id: Long): ProgressResponse =
        progressService.getProgressById(id)

    @GetMapping
    fun listProgresses(): List<ProgressResponse> =
        progressService.listProgresses()

    @DeleteMapping("/{id}")
    fun deleteProgress(@PathVariable id: Long) =
        progressService.deleteProgress(id)
}

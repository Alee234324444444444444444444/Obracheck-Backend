package com.cconstruct.construction.controllers

import com.cconstruct.construction.constants.Routes
import com.cconstruct.construction.models.requests.UploadEvidenceRequest
import com.cconstruct.construction.models.responses.EvidenceResponse
import com.cconstruct.construction.services.EvidenceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Routes.EVIDENCES)
class EvidenceController(
    private val evidenceService: EvidenceService
) {

    @PostMapping
    fun createEvidence(@RequestBody request: UploadEvidenceRequest): EvidenceResponse =
        evidenceService.createEvidence(request)

    @PutMapping("/{id}")
    fun updateEvidence(@PathVariable id: Long, @RequestBody request: UploadEvidenceRequest): EvidenceResponse =
        evidenceService.updateEvidence(id, request)

    @GetMapping("/{id}")
    fun getEvidenceById(@PathVariable id: Long): EvidenceResponse =
        evidenceService.getEvidenceById(id)

    @GetMapping
    fun listEvidences(): List<EvidenceResponse> =
        evidenceService.listEvidences()

    @DeleteMapping("/{id}")
    fun deleteEvidence(@PathVariable id: Long): ResponseEntity<Void> {
        evidenceService.deleteEvidence(id)
        return ResponseEntity.noContent().build()
    }
}

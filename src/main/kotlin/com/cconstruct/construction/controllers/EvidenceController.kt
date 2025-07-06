package com.cconstruct.construction.controllers

import com.cconstruct.construction.constants.Routes
import com.cconstruct.construction.models.responses.EvidenceListResponse
import com.cconstruct.construction.models.responses.EvidenceResponse
import com.cconstruct.construction.models.responses.EvidenceUploadResponse
import com.cconstruct.construction.services.EvidenceService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(Routes.EVIDENCES)
class EvidenceController(
    private val evidenceService: EvidenceService
) {

    @PostMapping("/upload")
    fun uploadEvidence(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("progressId") progressId: Long
    ): ResponseEntity<EvidenceUploadResponse> {
        val response = evidenceService.uploadEvidence(file, progressId)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun updateEvidence(
        @PathVariable id: Long,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<EvidenceUploadResponse> {
        val response = evidenceService.updateEvidence(id, file)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun listAll(): ResponseEntity<EvidenceListResponse> =
        ResponseEntity.ok(evidenceService.listEvidences())

    @GetMapping("/{id}")
    fun getEvidenceById(@PathVariable id: Long): ResponseEntity<EvidenceResponse> =
        ResponseEntity.ok(evidenceService.getEvidenceById(id))

    @GetMapping("/{id}/download")
    fun downloadEvidence(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val (evidence, bytes) = evidenceService.downloadEvidence(id)

        val headers = HttpHeaders().apply {
            contentType = MediaType.parseMediaType(evidence.contentType)
            setContentDispositionFormData("attachment", evidence.originalFileName)
            contentLength = bytes.size.toLong()
        }

        return ResponseEntity.ok()
            .headers(headers)
            .body(bytes)
    }

    @DeleteMapping("/{id}")
    fun deleteEvidence(@PathVariable id: Long): ResponseEntity<Void> {
        evidenceService.deleteEvidence(id)
        return ResponseEntity.noContent().build()
    }
}

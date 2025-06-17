package com.cconstruct.construction.services

import com.cconstruct.construction.exceptions.EvidenceNotFoundException
import com.cconstruct.construction.exceptions.ProgressNotFoundException
import com.cconstruct.construction.mappers.EvidenceMapper
import com.cconstruct.construction.models.entities.Evidence
import com.cconstruct.construction.models.requests.UploadEvidenceRequest
import com.cconstruct.construction.models.responses.EvidenceResponse
import com.cconstruct.construction.repositories.EvidenceRepository
import com.cconstruct.construction.repositories.ProgressRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.Base64

@Service
class EvidenceService(
    private val evidenceRepository: EvidenceRepository,
    private val progressRepository: ProgressRepository,
    private val evidenceMapper: EvidenceMapper
) {

    fun uploadEvidence(file: MultipartFile, progressId: Long): EvidenceResponse {
        val progress = progressRepository.findById(progressId)
            .orElseThrow { ProgressNotFoundException("Progress with ID $progressId not found.") }

        val evidence = Evidence(
            fileName = file.originalFilename ?: "unnamed",
            content = file.bytes,
            progress = progress
        )
        return evidenceMapper.toResponse(evidenceRepository.save(evidence))
    }

    fun updateEvidence(id: Long, request: UploadEvidenceRequest): EvidenceResponse {
        val evidence = evidenceRepository.findById(id)
            .orElseThrow { EvidenceNotFoundException("Evidence with ID $id not found.") }

        val progress = progressRepository.findById(request.progressId)
            .orElseThrow { ProgressNotFoundException("Progress with ID ${request.progressId} not found.") }

        val decodedContent = Base64.getDecoder().decode(request.content)

        evidence.fileName = request.fileName
        evidence.content = decodedContent
        evidence.progress = progress

        return evidenceMapper.toResponse(evidenceRepository.save(evidence))
    }

    fun getEvidenceById(id: Long): EvidenceResponse {
        val evidence = evidenceRepository.findById(id)
            .orElseThrow { EvidenceNotFoundException("Evidence with ID $id not found.") }
        return evidenceMapper.toResponse(evidence)
    }

    fun listEvidences(): List<EvidenceResponse> =
        evidenceMapper.toResponseList(evidenceRepository.findAll())

    fun deleteEvidence(id: Long) {
        val evidence = evidenceRepository.findById(id)
            .orElseThrow { EvidenceNotFoundException("Evidence with ID $id not found.") }
        evidenceRepository.delete(evidence)
    }
}

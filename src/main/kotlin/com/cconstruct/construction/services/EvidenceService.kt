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

@Service
class EvidenceService(
    private val evidenceRepository: EvidenceRepository,
    private val progressRepository: ProgressRepository,
    private val evidenceMapper: EvidenceMapper
) {

    fun createEvidence(request: UploadEvidenceRequest): EvidenceResponse {
        val progress = progressRepository.findById(request.progressId)
            .orElseThrow { ProgressNotFoundException("Progress with ID ${request.progressId} not found.") }

        val evidence = Evidence(
            fileName = request.fileName,
            progress = progress
        )

        return evidenceMapper.toResponse(evidenceRepository.save(evidence))
    }

    fun updateEvidence(id: Long, request: UploadEvidenceRequest): EvidenceResponse {
        val evidence = evidenceRepository.findById(id)
            .orElseThrow { EvidenceNotFoundException("Evidence with ID $id not found.") }

        val progress = progressRepository.findById(request.progressId)
            .orElseThrow { ProgressNotFoundException("Progress with ID ${request.progressId} not found.") }

        evidence.fileName = request.fileName
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

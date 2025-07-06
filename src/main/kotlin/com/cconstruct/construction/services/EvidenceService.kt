package com.cconstruct.construction.services

import com.cconstruct.construction.exceptions.EvidenceAlreadyExistsException
import com.cconstruct.construction.exceptions.EvidenceNotFoundException
import com.cconstruct.construction.exceptions.ProgressNotFoundException
import com.cconstruct.construction.mappers.EvidenceMapper
import com.cconstruct.construction.models.entities.Evidence
import com.cconstruct.construction.models.responses.EvidenceListResponse
import com.cconstruct.construction.models.responses.EvidenceResponse
import com.cconstruct.construction.models.responses.EvidenceUploadResponse
import com.cconstruct.construction.repositories.EvidenceRepository
import com.cconstruct.construction.repositories.ProgressRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class EvidenceService(
    private val evidenceRepository: EvidenceRepository,
    private val progressRepository: ProgressRepository,
    private val evidenceMapper: EvidenceMapper
) {

    fun uploadEvidence(file: MultipartFile, progressId: Long): EvidenceUploadResponse {
        if (file.isEmpty) {
            throw IllegalArgumentException("The uploaded file is empty.")
        }

        val contentType = file.contentType
        if (contentType == null || !contentType.startsWith("image/")) {
            throw IllegalArgumentException("Only image files are allowed.")
        }

        val originalName = file.originalFilename ?: "unnamed.jpg"

        if (evidenceRepository.existsByFileName(originalName)) {
            throw EvidenceAlreadyExistsException("An evidence with the name \"$originalName\" already exists.")
        }

        val progress = progressRepository.findById(progressId)
            .orElseThrow { ProgressNotFoundException("Progress with ID $progressId not found.") }

        val evidence = Evidence(
            fileName = originalName,
            originalFileName = originalName,
            contentType = contentType,
            fileSize = file.size,
            content = file.bytes,
            progress = progress
        )

        val saved = evidenceRepository.save(evidence)

        return EvidenceUploadResponse(
            message = "Image uploaded successfully.",
            image = evidenceMapper.toDto(saved)
        )
    }

    fun updateEvidence(id: Long, file: MultipartFile): EvidenceUploadResponse {
        if (file.isEmpty) {
            throw IllegalArgumentException("The uploaded file is empty.")
        }

        val contentType = file.contentType
        if (contentType == null || !contentType.startsWith("image/")) {
            throw IllegalArgumentException("Only image files are allowed.")
        }

        val originalName = file.originalFilename ?: "unnamed.jpg"

        val evidence = evidenceRepository.findById(id)
            .orElseThrow { EvidenceNotFoundException("Evidence with ID $id not found.") }

        if (evidenceRepository.existsByFileName(originalName) && evidence.fileName != originalName) {
            throw EvidenceAlreadyExistsException("An evidence with the name \"$originalName\" already exists.")
        }

        evidence.fileName = originalName
        evidence.originalFileName = originalName
        evidence.contentType = contentType
        evidence.fileSize = file.size
        evidence.content = file.bytes
        evidence.uploadDate = LocalDateTime.now()

        val updated = evidenceRepository.save(evidence)

        return EvidenceUploadResponse(
            message = "Image updated successfully.",
            image = evidenceMapper.toDto(updated)
        )
    }

    fun getEvidenceById(id: Long): EvidenceResponse {
        val evidence = evidenceRepository.findById(id)
            .orElseThrow { EvidenceNotFoundException("Evidence with ID $id not found.") }

        return evidenceMapper.toResponse(evidence)
    }

    fun downloadEvidence(id: Long): Pair<Evidence, ByteArray> {
        val evidence = evidenceRepository.findById(id)
            .orElseThrow { EvidenceNotFoundException("Evidence with ID $id not found.") }

        return Pair(evidence, evidence.content)
    }

    fun listEvidences(): EvidenceListResponse {
        val evidences = evidenceRepository.findAll()
        return EvidenceListResponse(
            images = evidenceMapper.toDtoList(evidences),
            total = evidences.size.toLong()
        )
    }

    fun deleteEvidence(id: Long) {
        val evidence = evidenceRepository.findById(id)
            .orElseThrow { EvidenceNotFoundException("Evidence with ID $id not found.") }

        evidenceRepository.delete(evidence)
    }
}

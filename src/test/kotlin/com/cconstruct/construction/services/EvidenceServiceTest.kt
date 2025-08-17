package com.cconstruct.construction.services

import com.cconstruct.construction.exceptions.*
import com.cconstruct.construction.mappers.EvidenceMapper
import com.cconstruct.construction.models.dtos.EvidenceDto
import com.cconstruct.construction.models.entities.Evidence
import com.cconstruct.construction.models.entities.Progress
import com.cconstruct.construction.models.entities.Site
import com.cconstruct.construction.models.entities.Worker
import com.cconstruct.construction.models.responses.EvidenceResponse
import com.cconstruct.construction.repositories.EvidenceRepository
import com.cconstruct.construction.repositories.ProgressRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import java.util.Optional
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

class EvidenceServiceTest {

    private lateinit var evidenceRepository: EvidenceRepository
    private lateinit var progressRepository: ProgressRepository
    private lateinit var evidenceMapper: EvidenceMapper
    private lateinit var evidenceService: EvidenceService
    private lateinit var file: MultipartFile

    @BeforeEach
    fun setUp() {
        evidenceRepository = mock(EvidenceRepository::class.java)
        progressRepository = mock(ProgressRepository::class.java)
        evidenceMapper = mock(EvidenceMapper::class.java)
        file = mock(MultipartFile::class.java)
        evidenceService = EvidenceService(evidenceRepository, progressRepository, evidenceMapper)
    }

    @Test
    fun should_upload_evidence_successfully() {
        val progress = Progress("Progreso", LocalDateTime.now(), mock(), mock()).apply { id = 1L }
        val evidence = Evidence("img.jpg", "img.jpg", "image/jpeg", 1000, ByteArray(10), progress)
        val dto = EvidenceDto(
            id = 1L,
            fileName = "img.jpg",
            originalFileName = "img.jpg",
            contentType = "image/jpeg",
            fileSize = 1000,
            uploadDate = evidence.uploadDate,
            progressId = 1L
        )

        `when`(file.isEmpty).thenReturn(false)
        `when`(file.contentType).thenReturn("image/jpeg")
        `when`(file.originalFilename).thenReturn("img.jpg")
        `when`(file.size).thenReturn(1000)
        `when`(file.bytes).thenReturn(ByteArray(10))
        `when`(evidenceRepository.existsByFileName("img.jpg")).thenReturn(false)
        `when`(progressRepository.findById(1L)).thenReturn(Optional.of(progress))
        `when`(evidenceRepository.save(any(Evidence::class.java))).thenReturn(evidence)
        `when`(evidenceMapper.toDto(evidence)).thenReturn(dto)

        val result = evidenceService.uploadEvidence(file, 1L)

        assertEquals("Image uploaded successfully.", result.message)
        assertEquals("img.jpg", result.image?.fileName)
    }

    @Test
    fun should_throw_exception_when_file_name_exists() {
        `when`(file.isEmpty).thenReturn(false)
        `when`(file.contentType).thenReturn("image/png")
        `when`(file.originalFilename).thenReturn("duplicate.png")
        `when`(evidenceRepository.existsByFileName("duplicate.png")).thenReturn(true)

        val exception = assertThrows<EvidenceAlreadyExistsException> {
            evidenceService.uploadEvidence(file, 1L)
        }

        assertEquals("An evidence with the name \"duplicate.png\" already exists.", exception.message)
    }

    @Test
    fun should_throw_exception_when_progress_not_found() {
        `when`(file.isEmpty).thenReturn(false)
        `when`(file.contentType).thenReturn("image/jpeg")
        `when`(file.originalFilename).thenReturn("img.jpg")
        `when`(evidenceRepository.existsByFileName("img.jpg")).thenReturn(false)
        `when`(progressRepository.findById(1L)).thenReturn(Optional.empty())

        val exception = assertThrows<ProgressNotFoundException> {
            evidenceService.uploadEvidence(file, 1L)
        }

        assertEquals("Progress with ID 1 not found.", exception.message)
    }

    @Test
    fun should_get_evidence_by_id() {
        val progress = Progress("Descripción", LocalDateTime.now(), mock(), mock()).apply { id = 1L }
        val evidence = Evidence("foto.png", "foto.png", "image/png", 1500, ByteArray(10), progress).apply { id = 5L }
        val response = EvidenceResponse(5L, "foto.png", 1L)

        `when`(evidenceRepository.findById(5L)).thenReturn(Optional.of(evidence))
        `when`(evidenceMapper.toResponse(evidence)).thenReturn(response)

        val result = evidenceService.getEvidenceById(5L)

        assertEquals(5L, result.id)
        assertEquals("foto.png", result.fileName)
    }

    @Test
    fun should_throw_exception_when_evidence_not_found_by_id() {
        `when`(evidenceRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<EvidenceNotFoundException> {
            evidenceService.getEvidenceById(99L)
        }
    }

    @Test
    fun should_list_all_evidences() {
        val progress = Progress("Desc", LocalDateTime.now(), mock(), mock()).apply { id = 1L }
        val evidence = Evidence("x.jpg", "x.jpg", "image/jpeg", 1000, ByteArray(10), progress).apply { id = 1L }
        val dto = EvidenceDto(
            id = 1L,
            fileName = "x.jpg",
            originalFileName = "x.jpg",
            contentType = "image/jpeg",
            fileSize = 1000,
            uploadDate = evidence.uploadDate,
            progressId = 1L
        )

        `when`(evidenceRepository.findAll()).thenReturn(listOf(evidence))
        `when`(evidenceMapper.toDtoList(listOf(evidence))).thenReturn(listOf(dto))

        val result = evidenceService.listEvidences()

        assertEquals(1, result.total)
        assertEquals("x.jpg", result.images[0].fileName)
    }

    @Test
    fun should_delete_evidence() {
        val progress = Progress("Avance", LocalDateTime.now(), mock(), mock())
        val evidence = Evidence("img.png", "img.png", "image/png", 800, ByteArray(10), progress).apply { id = 1L }

        `when`(evidenceRepository.findById(1L)).thenReturn(Optional.of(evidence))

        evidenceService.deleteEvidence(1L)

        verify(evidenceRepository).delete(evidence)
    }

    @Test
    fun should_throw_exception_when_deleting_nonexistent_evidence() {
        `when`(evidenceRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<EvidenceNotFoundException> {
            evidenceService.deleteEvidence(1L)
        }
    }

    @Test
    fun should_download_evidence_successfully() {
        val progress = Progress("Avance", LocalDateTime.now(), mock(), mock()).apply { id = 1L }
        val content = ByteArray(10)
        val evidence = Evidence("photo.jpg", "photo.jpg", "image/jpeg", 1234, content, progress).apply { id = 3L }

        `when`(evidenceRepository.findById(3L)).thenReturn(Optional.of(evidence))

        val (found, bytes) = evidenceService.downloadEvidence(3L)

        assertEquals("photo.jpg", found.fileName)
        assertArrayEquals(content, bytes)
    }

    @Test
    fun should_throw_exception_when_downloading_nonexistent_evidence() {
        `when`(evidenceRepository.findById(404L)).thenReturn(Optional.empty())

        assertThrows<EvidenceNotFoundException> {
            evidenceService.downloadEvidence(404L)
        }
    }

    @Test
    fun should_update_evidence_successfully() {
        val progress = Progress("Desc", LocalDateTime.now(), mock(), mock()).apply { id = 1L }
        val evidence = Evidence("old.jpg", "old.jpg", "image/jpeg", 500, ByteArray(5), progress).apply { id = 2L }
        val updated = evidence.copy(
            fileName = "new.jpg",
            originalFileName = "new.jpg",
            contentType = "image/png",
            fileSize = 1000,
            content = ByteArray(10)
        )
        val dto = EvidenceDto(
            id = 2L,
            fileName = "new.jpg",
            originalFileName = "new.jpg",
            contentType = "image/png",
            fileSize = 1000,
            uploadDate = LocalDateTime.now(),
            progressId = 1L
        )

        `when`(file.isEmpty).thenReturn(false)
        `when`(file.contentType).thenReturn("image/png")
        `when`(file.originalFilename).thenReturn("new.jpg")
        `when`(file.size).thenReturn(1000)
        `when`(file.bytes).thenReturn(ByteArray(10))
        `when`(evidenceRepository.findById(2L)).thenReturn(Optional.of(evidence))
        `when`(evidenceRepository.existsByFileName("new.jpg")).thenReturn(false)
        `when`(evidenceRepository.save(any(Evidence::class.java))).thenReturn(updated)
        `when`(evidenceMapper.toDto(updated)).thenReturn(dto)

        val result = evidenceService.updateEvidence(2L, file)

        assertEquals("Image updated successfully.", result.message)
        assertEquals("new.jpg", result.image?.fileName)
    }

    @Test
    fun should_throw_exception_when_updating_nonexistent_evidence() {
        `when`(file.isEmpty).thenReturn(false)
        `when`(file.contentType).thenReturn("image/jpeg")
        `when`(file.originalFilename).thenReturn("img.jpg")
        `when`(evidenceRepository.findById(99L)).thenReturn(Optional.empty())

        val exception = assertThrows<EvidenceNotFoundException> {
            evidenceService.updateEvidence(99L, file)
        }

        assertEquals("Evidence with ID 99 not found.", exception.message)
    }

    @Test
    fun should_update_evidence_when_file_name_is_same_as_existing() {
        val progress = Progress("Desc", LocalDateTime.now(), mock(), mock()).apply { id = 1L }
        val evidence = Evidence("same.jpg", "same.jpg", "image/jpeg", 500, ByteArray(5), progress).apply { id = 2L }
        val updated = evidence.copy(fileSize = 800, content = ByteArray(8))
        val dto = EvidenceDto(
            id = 2L,
            fileName = "same.jpg",
            originalFileName = "same.jpg",
            contentType = "image/jpeg",
            fileSize = 800,
            uploadDate = LocalDateTime.now(),
            progressId = 1L
        )

        `when`(file.isEmpty).thenReturn(false)
        `when`(file.contentType).thenReturn("image/jpeg")
        `when`(file.originalFilename).thenReturn("same.jpg")
        `when`(file.size).thenReturn(800)
        `when`(file.bytes).thenReturn(ByteArray(8))

        `when`(evidenceRepository.findById(2L)).thenReturn(Optional.of(evidence))
        `when`(evidenceRepository.existsByFileName("same.jpg")).thenReturn(true)
        `when`(evidenceRepository.save(evidence)).thenReturn(updated)
        `when`(evidenceMapper.toDto(updated)).thenReturn(dto)

        val result = evidenceService.updateEvidence(2L, file)

        assertEquals("Image updated successfully.", result.message)
        assertEquals("same.jpg", result.image?.fileName)
    }

    @Test
    fun should_throw_exception_when_updating_with_existing_different_file_name() {
        val progress = Progress("Test", LocalDateTime.now(), mock(), mock()).apply { id = 1L }
        val existingEvidence = Evidence("old.jpg", "old.jpg", "image/jpeg", 500, ByteArray(5), progress).apply { id = 2L }

        `when`(file.isEmpty).thenReturn(false)
        `when`(file.contentType).thenReturn("image/jpeg")
        `when`(file.originalFilename).thenReturn("new.jpg")
        `when`(file.size).thenReturn(1000)
        `when`(file.bytes).thenReturn(ByteArray(10))

        `when`(evidenceRepository.findById(2L)).thenReturn(Optional.of(existingEvidence))
        `when`(evidenceRepository.existsByFileName("new.jpg")).thenReturn(true)

        val exception = assertThrows<EvidenceAlreadyExistsException> {
            evidenceService.updateEvidence(2L, file)
        }

        assertEquals("An evidence with the name \"new.jpg\" already exists.", exception.message)
    }

    @Test
    fun should_update_evidence_with_valid_image_type() {
        val progress = Progress("Valid", LocalDateTime.now(), mock(), mock()).apply { id = 1L }
        val evidence = Evidence("valid.jpg", "valid.jpg", "image/jpeg", 500, ByteArray(5), progress).apply { id = 2L }
        val dto = EvidenceDto(
            id = 2L,
            fileName = "valid.jpg",
            originalFileName = "valid.jpg",
            contentType = "image/jpeg",
            fileSize = 1200,
            uploadDate = LocalDateTime.now(),
            progressId = 1L
        )

        `when`(file.isEmpty).thenReturn(false)
        `when`(file.contentType).thenReturn("image/jpeg")
        `when`(file.originalFilename).thenReturn("valid.jpg")
        `when`(file.size).thenReturn(1200)
        `when`(file.bytes).thenReturn(ByteArray(1200))

        `when`(evidenceRepository.findById(2L)).thenReturn(Optional.of(evidence))
        `when`(evidenceRepository.existsByFileName("valid.jpg")).thenReturn(true)
        `when`(evidenceRepository.save(evidence)).thenReturn(evidence)
        `when`(evidenceMapper.toDto(evidence)).thenReturn(dto)

        val result = evidenceService.updateEvidence(2L, file)

        assertEquals("Image updated successfully.", result.message)
        assertEquals("valid.jpg", result.image?.fileName)
    }

    @Test
    fun should_upload_image_when_content_type_is_valid() {
        val progress = Progress("Subida", LocalDateTime.now(), mock(), mock()).apply { id = 3L }
        val evidence = Evidence("foto.jpg", "foto.jpg", "image/jpeg", 2048, ByteArray(2048), progress)

        `when`(file.isEmpty).thenReturn(false)
        `when`(file.contentType).thenReturn("image/jpeg")
        `when`(file.originalFilename).thenReturn("foto.jpg")
        `when`(file.size).thenReturn(2048)
        `when`(file.bytes).thenReturn(ByteArray(2048))
        `when`(evidenceRepository.existsByFileName("foto.jpg")).thenReturn(false)
        `when`(progressRepository.findById(3L)).thenReturn(Optional.of(progress))
        `when`(evidenceRepository.save(any(Evidence::class.java))).thenReturn(evidence)
        `when`(evidenceMapper.toDto(evidence)).thenReturn(
            EvidenceDto(
                id = 3L,
                fileName = "foto.jpg",
                originalFileName = "foto.jpg",
                contentType = "image/jpeg",
                fileSize = 2048,
                uploadDate = LocalDateTime.now(),
                progressId = 3L
            )
        )

        val result = evidenceService.uploadEvidence(file, 3L)

        assertEquals("Image uploaded successfully.", result.message)
        assertEquals("foto.jpg", result.image?.fileName)
    }

    @Test
    fun should_upload_evidence_with_valid_content_type() {
        val progress = Progress("Subida", LocalDateTime.now(), mock(), mock()).apply { id = 10L }
        val evidence = Evidence("foto.jpg", "foto.jpg", "image/jpeg", 2048, ByteArray(2048), progress)
        val dto = EvidenceDto(
            id = 10L,
            fileName = "foto.jpg",
            originalFileName = "foto.jpg",
            contentType = "image/jpeg",
            fileSize = 2048,
            uploadDate = LocalDateTime.now(),
            progressId = 10L
        )

        `when`(file.isEmpty).thenReturn(false)
        `when`(file.contentType).thenReturn("image/jpeg")
        `when`(file.originalFilename).thenReturn("foto.jpg")
        `when`(file.size).thenReturn(2048)
        `when`(file.bytes).thenReturn(ByteArray(2048))

        `when`(evidenceRepository.existsByFileName("foto.jpg")).thenReturn(false)
        `when`(progressRepository.findById(10L)).thenReturn(Optional.of(progress))
        `when`(evidenceRepository.save(any(Evidence::class.java))).thenReturn(evidence)
        `when`(evidenceMapper.toDto(evidence)).thenReturn(dto)

        val result = evidenceService.uploadEvidence(file, 10L)

        assertEquals("Image uploaded successfully.", result.message)
        assertEquals("foto.jpg", result.image?.fileName)
    }

    @Test
    fun should_use_default_name_if_original_filename_is_null() {
        val progress = Progress("Test", LocalDateTime.now(), mock(), mock()).apply { id = 1L }
        val evidence = Evidence("unnamed.jpg", "unnamed.jpg", "image/jpeg", 1024, ByteArray(1024), progress)
        val dto = EvidenceDto(
            id = 11L,
            fileName = "unnamed.jpg",
            originalFileName = "unnamed.jpg",
            contentType = "image/jpeg",
            fileSize = 1024,
            uploadDate = LocalDateTime.now(),
            progressId = 1L
        )

        `when`(file.isEmpty).thenReturn(false)
        `when`(file.contentType).thenReturn("image/jpeg")
        `when`(file.originalFilename).thenReturn(null)
        `when`(file.size).thenReturn(1024)
        `when`(file.bytes).thenReturn(ByteArray(1024))

        `when`(evidenceRepository.existsByFileName("unnamed.jpg")).thenReturn(false)
        `when`(progressRepository.findById(1L)).thenReturn(Optional.of(progress))
        `when`(evidenceRepository.save(any())).thenReturn(evidence)
        `when`(evidenceMapper.toDto(evidence)).thenReturn(dto)

        val result = evidenceService.uploadEvidence(file, 1L)

        assertEquals("Image uploaded successfully.", result.message)
        assertEquals("unnamed.jpg", result.image?.fileName)
    }

    @Test
    fun should_use_default_name_when_updating_if_original_filename_is_null() {
        val progress = Progress("Modificación", LocalDateTime.now(), mock(), mock()).apply { id = 20L }
        val evidence = Evidence("old.jpg", "old.jpg", "image/jpeg", 500, ByteArray(5), progress).apply { id = 5L }
        val dto = EvidenceDto(
            id = 5L,
            fileName = "unnamed.jpg",
            originalFileName = "unnamed.jpg",
            contentType = "image/jpeg",
            fileSize = 1024,
            uploadDate = LocalDateTime.now(),
            progressId = 20L
        )

        `when`(file.isEmpty).thenReturn(false)
        `when`(file.contentType).thenReturn("image/jpeg")
        `when`(file.originalFilename).thenReturn(null)
        `when`(file.size).thenReturn(1024)
        `when`(file.bytes).thenReturn(ByteArray(1024))

        `when`(evidenceRepository.findById(5L)).thenReturn(Optional.of(evidence))
        `when`(evidenceRepository.existsByFileName("unnamed.jpg")).thenReturn(false)
        `when`(evidenceRepository.save(evidence)).thenReturn(evidence)
        `when`(evidenceMapper.toDto(evidence)).thenReturn(dto)

        val result = evidenceService.updateEvidence(5L, file)

        assertEquals("Image updated successfully.", result.message)
        assertEquals("unnamed.jpg", result.image?.fileName)
    }

    @Test
    fun should_update_evidence_using_default_name_if_original_filename_is_null() {
        val progress = Progress("Actualización", LocalDateTime.now(), mock(), mock()).apply { id = 30L }
        val evidence = Evidence("prev.jpg", "prev.jpg", "image/jpeg", 600, ByteArray(600), progress).apply { id = 6L }
        val dto = EvidenceDto(
            id = 6L,
            fileName = "unnamed.jpg",
            originalFileName = "unnamed.jpg",
            contentType = "image/jpeg",
            fileSize = 1500,
            uploadDate = LocalDateTime.now(),
            progressId = 30L
        )

        `when`(file.isEmpty).thenReturn(false)
        `when`(file.contentType).thenReturn("image/jpeg")
        `when`(file.originalFilename).thenReturn(null)
        `when`(file.size).thenReturn(1500)
        `when`(file.bytes).thenReturn(ByteArray(1500))

        `when`(evidenceRepository.findById(6L)).thenReturn(Optional.of(evidence))
        `when`(evidenceRepository.existsByFileName("unnamed.jpg")).thenReturn(false)
        `when`(evidenceRepository.save(evidence)).thenReturn(evidence)
        `when`(evidenceMapper.toDto(evidence)).thenReturn(dto)

        val result = evidenceService.updateEvidence(6L, file)

        assertEquals("Image updated successfully.", result.message)
        assertEquals("unnamed.jpg", result.image?.fileName)
    }

    @Test
    fun should_upload_evidence_using_default_name_if_original_filename_is_null() {
        val progress = Progress("Subida", LocalDateTime.now(), mock(), mock()).apply { id = 20L }
        val evidence = Evidence("unnamed.jpg", "unnamed.jpg", "image/jpeg", 1000, ByteArray(1000), progress)
        val dto = EvidenceDto(
            id = 20L,
            fileName = "unnamed.jpg",
            originalFileName = "unnamed.jpg",
            contentType = "image/jpeg",
            fileSize = 1000,
            uploadDate = LocalDateTime.now(),
            progressId = 20L
        )

        `when`(file.isEmpty).thenReturn(false)
        `when`(file.contentType).thenReturn("image/jpeg")
        `when`(file.originalFilename).thenReturn(null)
        `when`(file.size).thenReturn(1000)
        `when`(file.bytes).thenReturn(ByteArray(1000))

        `when`(evidenceRepository.existsByFileName("unnamed.jpg")).thenReturn(false)
        `when`(progressRepository.findById(20L)).thenReturn(Optional.of(progress))
        `when`(evidenceRepository.save(any(Evidence::class.java))).thenReturn(evidence)
        `when`(evidenceMapper.toDto(evidence)).thenReturn(dto)

        val result = evidenceService.uploadEvidence(file, 20L)

        assertEquals("Image uploaded successfully.", result.message)
        assertEquals("unnamed.jpg", result.image?.fileName)
    }

    @Test
    fun should_use_default_values_when_file_fields_are_null() {
        `when`(file.contentType).thenReturn(null)
        `when`(file.originalFilename).thenReturn(null)
        `when`(file.size).thenReturn(123L)
        `when`(file.bytes).thenReturn("bytes".toByteArray())

        val site = mock(Site::class.java)
        val worker = mock(Worker::class.java)

        val progress = Progress(
            date = LocalDateTime.now(),
            description = "Test description",
            site = site,
            worker = worker
        ).apply { id = 1L }

        `when`(progressRepository.findById(1L)).thenReturn(Optional.of(progress))
        `when`(evidenceRepository.existsByFileName("unnamed.jpg")).thenReturn(false)

        val savedEvidence = Evidence(
            fileName = "unnamed.jpg",
            originalFileName = "unnamed.jpg",
            contentType = "application/octet-stream",
            fileSize = 123L,
            content = "bytes".toByteArray(),
            progress = progress
        )

        `when`(evidenceRepository.save(any(Evidence::class.java))).thenReturn(savedEvidence)

        val expectedDto = EvidenceDto(
            id = 10L,
            fileName = "unnamed.jpg",
            originalFileName = "unnamed.jpg",
            contentType = "application/octet-stream",
            fileSize = 123L,
            uploadDate = LocalDateTime.now(),
            progressId = 1L
        )

        `when`(evidenceMapper.toDto(savedEvidence)).thenReturn(expectedDto)

        val result = evidenceService.uploadEvidence(file, 1L)

        assertEquals("Image uploaded successfully.", result.message)
        assertEquals("unnamed.jpg", result.image?.fileName)
        verify(evidenceRepository).save(any(Evidence::class.java))
    }

    @Test
    fun should_update_evidence_with_default_values_when_fields_are_null() {
        `when`(file.contentType).thenReturn(null)
        `when`(file.originalFilename).thenReturn(null)
        `when`(file.size).thenReturn(456L)
        `when`(file.bytes).thenReturn("updated".toByteArray())

        val site = mock(Site::class.java)
        val worker = mock(Worker::class.java)

        val progress = Progress(
            date = LocalDateTime.now(),
            description = "desc",
            site = site,
            worker = worker
        ).apply { id = 1L }

        val existingEvidence = Evidence(
            fileName = "oldname.jpg",
            originalFileName = "oldname.jpg",
            contentType = "image/jpeg",
            fileSize = 999L,
            content = "oldcontent".toByteArray(),
            progress = progress,
            uploadDate = LocalDateTime.now()
        )

        val updatedEvidence = Evidence(
            fileName = "unnamed.jpg",
            originalFileName = "unnamed.jpg",
            contentType = "application/octet-stream",
            fileSize = 456L,
            content = "updated".toByteArray(),
            progress = progress
        ).apply { id = 10L }

        val expectedDto = EvidenceDto(
            id = 10L,
            fileName = "unnamed.jpg",
            originalFileName = "unnamed.jpg",
            contentType = "application/octet-stream",
            fileSize = 456L,
            uploadDate = LocalDateTime.now(),
            progressId = 1L
        )

        `when`(evidenceRepository.findById(10L)).thenReturn(Optional.of(existingEvidence))
        `when`(evidenceRepository.existsByFileName("unnamed.jpg")).thenReturn(false)
        `when`(evidenceRepository.save(any(Evidence::class.java))).thenReturn(updatedEvidence)
        `when`(evidenceMapper.toDto(updatedEvidence)).thenReturn(expectedDto)

        val result = evidenceService.updateEvidence(10L, file)

        assertEquals("Image updated successfully.", result.message)
        assertEquals("unnamed.jpg", result.image?.fileName)
        assertEquals("application/octet-stream", result.image?.contentType)
        assertEquals(456L, result.image?.fileSize)
        assertEquals(10L, result.image?.id)
        assertNotNull(result.image?.uploadDate)

        verify(evidenceRepository).save(any(Evidence::class.java))
    }
}

package com.cconstruct.construction.services

import com.cconstruct.construction.exceptions.ProgressNotFoundException
import com.cconstruct.construction.exceptions.SiteNotFoundException
import com.cconstruct.construction.exceptions.WorkerNotFoundException
import com.cconstruct.construction.mappers.ProgressMapper
import com.cconstruct.construction.models.entities.*
import com.cconstruct.construction.models.requests.CreateProgressRequest
import com.cconstruct.construction.models.responses.*
import com.cconstruct.construction.repositories.ProgressRepository
import com.cconstruct.construction.repositories.SiteRepository
import com.cconstruct.construction.repositories.WorkerRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.time.LocalDateTime
import java.util.*

class ProgressServiceTest {

    private lateinit var progressRepository: ProgressRepository
    private lateinit var siteRepository: SiteRepository
    private lateinit var workerRepository: WorkerRepository
    private lateinit var progressMapper: ProgressMapper
    private lateinit var progressService: ProgressService

    @BeforeEach
    fun setUp() {
        progressRepository = mock(ProgressRepository::class.java)
        siteRepository = mock(SiteRepository::class.java)
        workerRepository = mock(WorkerRepository::class.java)
        progressMapper = mock(ProgressMapper::class.java)
        progressService = ProgressService(progressRepository, siteRepository, workerRepository, progressMapper)
    }

    @Test
    fun should_create_progress() {
        val site = Site("Obra 1", "Dirección 1", mock()).apply { id = 1L }
        val worker = Worker("Carlos", "Albañil", "1234567890", site).apply { id = 1L }
        val request = CreateProgressRequest("Avance realizado", LocalDateTime.now(), 1L, 1L)
        val progress = Progress(request.description, request.date, site, worker).apply { id = 1L }
        val response = ProgressResponse(1L, request.description, request.date,
            SiteSummaryResponse(1L, "Obra 1"),
            WorkerSummaryResponse(1L, "Carlos", "Albañil"),
            listOf()
        )

        `when`(siteRepository.findById(1L)).thenReturn(Optional.of(site))
        `when`(workerRepository.findById(1L)).thenReturn(Optional.of(worker))
        `when`(progressRepository.save(any(Progress::class.java))).thenReturn(progress)
        `when`(progressMapper.toResponse(progress)).thenReturn(response)

        val result = progressService.createProgress(request)

        assertEquals("Avance realizado", result.description)
        assertEquals("Carlos", result.worker.name)
    }

    @Test
    fun should_throw_SiteNotFoundException_when_creating_with_invalid_site() {
        val request = CreateProgressRequest("Avance", LocalDateTime.now(), 99L, 1L)
        `when`(siteRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<SiteNotFoundException> {
            progressService.createProgress(request)
        }
    }

    @Test
    fun should_throw_WorkerNotFoundException_when_creating_with_invalid_worker() {
        val site = Site("Obra", "Calle", mock()).apply { id = 1L }
        val request = CreateProgressRequest("Avance", LocalDateTime.now(), 1L, 99L)

        `when`(siteRepository.findById(1L)).thenReturn(Optional.of(site))
        `when`(workerRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<WorkerNotFoundException> {
            progressService.createProgress(request)
        }
    }

    @Test
    fun should_return_all_progresses() {
        val site = Site("Obra Norte", "Av. Siempre Viva", mock()).apply { id = 1L }
        val worker = Worker("Pedro", "Albañil", "1234567890", site).apply { id = 1L }
        val progress = Progress("Avance 1", LocalDateTime.now(), site, worker).apply { id = 1L }
        val response = ProgressResponse(1L, "Avance 1", progress.date,
            SiteSummaryResponse(1L, "Obra Norte"),
            WorkerSummaryResponse(1L, "Pedro", "Albañil"),
            listOf()
        )

        `when`(progressRepository.findAll()).thenReturn(listOf(progress))
        `when`(progressMapper.toResponseList(listOf(progress))).thenReturn(listOf(response))

        val result = progressService.listProgresses()

        assertEquals(1, result.size)
        assertEquals("Avance 1", result[0].description)
    }

    @Test
    fun should_return_progress_by_id() {
        val site = Site("Obra Sur", "Av. Ecuador", mock()).apply { id = 1L }
        val worker = Worker("Luis", "Maestro", "9876543210", site).apply { id = 1L }
        val progress = Progress("Revisión", LocalDateTime.now(), site, worker).apply { id = 1L }
        val response = ProgressResponse(1L, "Revisión", progress.date,
            SiteSummaryResponse(1L, "Obra Sur"),
            WorkerSummaryResponse(1L, "Luis", "Maestro"),
            listOf()
        )

        `when`(progressRepository.findById(1L)).thenReturn(Optional.of(progress))
        `when`(progressMapper.toResponse(progress)).thenReturn(response)

        val result = progressService.getProgressById(1L)

        assertEquals("Revisión", result.description)
        assertEquals("Luis", result.worker.name)
    }

    @Test
    fun should_throw_ProgressNotFoundException_when_getting_by_invalid_id() {
        `when`(progressRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<ProgressNotFoundException> {
            progressService.getProgressById(1L)
        }
    }

    @Test
    fun should_update_progress() {
        val site = Site("Obra A", "Calle Falsa 123", mock()).apply { id = 1L }
        val worker = Worker("Ana", "Ingeniera", "0000000000", site).apply { id = 1L }
        val progress = Progress("Viejo avance", LocalDateTime.now(), site, worker).apply { id = 1L }
        val request = CreateProgressRequest("Nuevo avance", LocalDateTime.now(), 1L, 1L)
        val response = ProgressResponse(1L, "Nuevo avance", request.date,
            SiteSummaryResponse(1L, "Obra A"),
            WorkerSummaryResponse(1L, "Ana", "Ingeniera"),
            listOf()
        )

        `when`(progressRepository.findById(1L)).thenReturn(Optional.of(progress))
        `when`(siteRepository.findById(1L)).thenReturn(Optional.of(site))
        `when`(workerRepository.findById(1L)).thenReturn(Optional.of(worker))
        `when`(progressRepository.save(progress)).thenReturn(progress)
        `when`(progressMapper.toResponse(progress)).thenReturn(response)

        val result = progressService.updateProgress(1L, request)

        assertEquals("Nuevo avance", result.description)
    }

    @Test
    fun should_throw_exception_when_updating_nonexistent_progress() {
        val request = CreateProgressRequest("Nuevo", LocalDateTime.now(), 1L, 1L)
        `when`(progressRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<ProgressNotFoundException> {
            progressService.updateProgress(1L, request)
        }
    }

    @Test
    fun should_throw_exception_when_updating_with_nonexistent_site() {
        val site = Site("Obra X", "Lugar", mock()).apply { id = 1L }
        val worker = Worker("Mauro", "Oficial", "111", site).apply { id = 1L }
        val progress = Progress("Progreso", LocalDateTime.now(), site, worker).apply { id = 1L }
        val request = CreateProgressRequest("Texto", LocalDateTime.now(), 99L, 1L)

        `when`(progressRepository.findById(1L)).thenReturn(Optional.of(progress))
        `when`(siteRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<SiteNotFoundException> {
            progressService.updateProgress(1L, request)
        }
    }

    @Test
    fun should_throw_exception_when_updating_with_nonexistent_worker() {
        val site = Site("Obra Y", "Otra", mock()).apply { id = 1L }
        val worker = Worker("Mario", "Oficial", "222", site).apply { id = 1L }
        val progress = Progress("Progreso", LocalDateTime.now(), site, worker).apply { id = 1L }
        val request = CreateProgressRequest("Texto", LocalDateTime.now(), 1L, 99L)

        `when`(progressRepository.findById(1L)).thenReturn(Optional.of(progress))
        `when`(siteRepository.findById(1L)).thenReturn(Optional.of(site))
        `when`(workerRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<WorkerNotFoundException> {
            progressService.updateProgress(1L, request)
        }
    }

    @Test
    fun should_delete_progress() {
        val site = Site("Obra Z", "Dirección Z", mock()).apply { id = 1L }
        val worker = Worker("Diego", "Obrero", "2222222222", site).apply { id = 1L }
        val progress = Progress("Eliminar esto", LocalDateTime.now(), site, worker).apply { id = 1L }

        `when`(progressRepository.findById(1L)).thenReturn(Optional.of(progress))

        progressService.deleteProgress(1L)

        verify(progressRepository).delete(progress)
    }

    @Test
    fun should_throw_exception_when_deleting_nonexistent_progress() {
        `when`(progressRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<ProgressNotFoundException> {
            progressService.deleteProgress(1L)
        }
    }
}

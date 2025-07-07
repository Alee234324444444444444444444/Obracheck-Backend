package com.cconstruct.construction.services

import com.cconstruct.construction.exceptions.*
import com.cconstruct.construction.mappers.WorkerMapper
import com.cconstruct.construction.models.entities.Site
import com.cconstruct.construction.models.entities.Worker
import com.cconstruct.construction.models.requests.CreateWorkerRequest
import com.cconstruct.construction.models.responses.SiteSummaryResponse
import com.cconstruct.construction.models.responses.WorkerResponse
import com.cconstruct.construction.repositories.SiteRepository
import com.cconstruct.construction.repositories.WorkerRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

class WorkerServiceTest {

    private lateinit var workerRepository: WorkerRepository
    private lateinit var siteRepository: SiteRepository
    private lateinit var workerMapper: WorkerMapper
    private lateinit var workerService: WorkerService

    @BeforeEach
    fun setUp() {
        workerRepository = mock(WorkerRepository::class.java)
        siteRepository = mock(SiteRepository::class.java)
        workerMapper = mock(WorkerMapper::class.java)
        workerService = WorkerService(workerRepository, siteRepository, workerMapper)
    }

    @Test
    fun should_create_a_new_worker() {
        val request = CreateWorkerRequest("Pedro", "Albañil", "1234567890", 1L)
        val site = Site("Obra Norte", "Av. Siempre Viva", mock())
        val worker = Worker("Pedro", "Albañil", "1234567890", site).apply { id = 1L }
        val response = WorkerResponse(1L, "Pedro", "Albañil", "1234567890", SiteSummaryResponse(1L, "Obra Norte"))

        `when`(workerRepository.existsByCi("1234567890")).thenReturn(false)
        `when`(siteRepository.findById(1L)).thenReturn(Optional.of(site))
        `when`(workerRepository.save(any(Worker::class.java))).thenReturn(worker)
        `when`(workerMapper.toResponse(worker)).thenReturn(response)

        val result = workerService.createWorker(request)

        assertEquals("Pedro", result.name)
        assertEquals("Albañil", result.role)
    }

    @Test
    fun should_throw_exception_when_worker_with_CI_exists() {
        val request = CreateWorkerRequest("Pedro", "Albañil", "1234567890", 1L)
        `when`(workerRepository.existsByCi("1234567890")).thenReturn(true)

        assertThrows<WorkerAlreadyExistsException> {
            workerService.createWorker(request)
        }
    }

    @Test
    fun should_return_worker_by_ID() {
        val site = Site("Obra Norte", "Av. Siempre Viva", mock())
        val worker = Worker("Pedro", "Albañil", "1234567890", site).apply { id = 1L }
        val response = WorkerResponse(1L, "Pedro", "Albañil", "1234567890", SiteSummaryResponse(1L, "Obra Norte"))

        `when`(workerRepository.findById(1L)).thenReturn(Optional.of(worker))
        `when`(workerMapper.toResponse(worker)).thenReturn(response)

        val result = workerService.getWorkerById(1L)

        assertEquals("Pedro", result.name)
    }

    @Test
    fun should_throw_exception_when_worker_not_found() {
        `when`(workerRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<WorkerNotFoundException> {
            workerService.getWorkerById(1L)
        }
    }

    @Test
    fun should_return_list_of_workers() {
        val site = Site("Obra Norte", "Av. Siempre Viva", mock())
        val worker = Worker("Pedro", "Albañil", "1234567890", site).apply { id = 1L }
        val response = WorkerResponse(1L, "Pedro", "Albañil", "1234567890", SiteSummaryResponse(1L, "Obra Norte"))

        `when`(workerRepository.findAll()).thenReturn(listOf(worker))
        `when`(workerMapper.toResponseList(listOf(worker))).thenReturn(listOf(response))

        val result = workerService.listWorkers()

        assertEquals(1, result.size)
        assertEquals("Pedro", result[0].name)
    }

    @Test
    fun should_update_worker() {
        val site = Site("Obra Norte", "Av. Siempre Viva", mock())
        val updatedSite = Site("Obra Sur", "Calle 123", mock()).apply { id = 2L }
        val existingWorker = Worker("Pedro", "Albañil", "1234567890", site).apply { id = 1L }
        val request = CreateWorkerRequest("Juan", "Maestro", "1234567890", 2L)
        val updatedWorker = Worker("Juan", "Maestro", "1234567890", updatedSite).apply { id = 1L }
        val response = WorkerResponse(1L, "Juan", "Maestro", "1234567890", SiteSummaryResponse(2L, "Obra Sur"))

        `when`(workerRepository.findById(1L)).thenReturn(Optional.of(existingWorker))
        `when`(siteRepository.findById(2L)).thenReturn(Optional.of(updatedSite))
        `when`(workerRepository.save(existingWorker)).thenReturn(updatedWorker)
        `when`(workerMapper.toResponse(updatedWorker)).thenReturn(response)

        val result = workerService.updateWorker(1L, request)

        assertEquals("Juan", result.name)
        assertEquals("Maestro", result.role)
    }

    @Test
    fun should_delete_worker() {
        val site = Site("Obra Norte", "Av. Siempre Viva", mock())
        val worker = Worker("Pedro", "Albañil", "1234567890", site).apply { id = 1L }

        `when`(workerRepository.findById(1L)).thenReturn(Optional.of(worker))

        workerService.deleteWorker(1L)

        verify(workerRepository).delete(worker)
    }

    @Test
    fun should_throw_exception_when_deleting_nonexistent_worker() {
        `when`(workerRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<WorkerNotFoundException> {
            workerService.deleteWorker(1L)
        }
    }
}
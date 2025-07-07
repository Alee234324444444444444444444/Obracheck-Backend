package com.cconstruct.construction.services

import com.cconstruct.construction.exceptions.*
import com.cconstruct.construction.mappers.SiteMapper
import com.cconstruct.construction.models.entities.Site
import com.cconstruct.construction.models.entities.User
import com.cconstruct.construction.models.requests.CreateSiteRequest
import com.cconstruct.construction.models.responses.SiteResponse
import com.cconstruct.construction.models.responses.UserResponse
import com.cconstruct.construction.repositories.SiteRepository
import com.cconstruct.construction.repositories.UserRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

class SiteServiceTest {

    private lateinit var siteRepository: SiteRepository
    private lateinit var userRepository: UserRepository
    private lateinit var siteMapper: SiteMapper
    private lateinit var siteService: SiteService

    @BeforeEach
    fun setUp() {
        siteRepository = mock(SiteRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        siteMapper = mock(SiteMapper::class.java)
        siteService = SiteService(siteRepository, userRepository, siteMapper)
    }

    @Test
    fun should_create_site_successfully() {
        val request = CreateSiteRequest("Obra Norte", "Quito", 1L)
        val user = User(name = "Admin", email = "admin@obra.com").apply { id = 1L }
        val site = Site(name = request.name, address = request.address, user = user).apply { id = 2L }
        val response = SiteResponse(2L, "Obra Norte", "Quito", listOf(), listOf(), UserResponse(1L, "Admin", "admin@obra.com"))

        `when`(siteRepository.existsByNameAndAddress(request.name, request.address)).thenReturn(false)
        `when`(userRepository.findById(request.userId)).thenReturn(Optional.of(user))
        `when`(siteRepository.save(any(Site::class.java))).thenReturn(site)
        `when`(siteMapper.toResponse(site)).thenReturn(response)

        val result = siteService.createSite(request)

        assertEquals("Obra Norte", result.name)
        assertEquals("Quito", result.address)
    }

    @Test
    fun should_throw_exception_when_site_exists() {
        val request = CreateSiteRequest("Obra Norte", "Quito", 1L)
        `when`(siteRepository.existsByNameAndAddress(request.name, request.address)).thenReturn(true)

        assertThrows<SiteAlreadyExistsException> {
            siteService.createSite(request)
        }
    }

    @Test
    fun should_throw_exception_when_user_not_found_on_create() {
        val request = CreateSiteRequest("Obra Norte", "Quito", 99L)
        `when`(siteRepository.existsByNameAndAddress(request.name, request.address)).thenReturn(false)
        `when`(userRepository.findById(request.userId)).thenReturn(Optional.empty())

        assertThrows<UserNotFoundException> {
            siteService.createSite(request)
        }
    }

    @Test
    fun should_return_site_by_id() {
        val user = User("Admin", "admin@obra.com").apply { id = 1L }
        val site = Site("Obra Norte", "Quito", user).apply { id = 10L }
        val response = SiteResponse(10L, "Obra Norte", "Quito", listOf(), listOf(), UserResponse(1L, "Admin", "admin@obra.com"))

        `when`(siteRepository.findById(10L)).thenReturn(Optional.of(site))
        `when`(siteMapper.toResponse(site)).thenReturn(response)

        val result = siteService.getSiteById(10L)

        assertEquals("Obra Norte", result.name)
    }

    @Test
    fun should_throw_exception_when_site_not_found_by_id() {
        `when`(siteRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<SiteNotFoundException> {
            siteService.getSiteById(99L)
        }
    }

    @Test
    fun should_list_all_sites() {
        val user = User("Admin", "admin@obra.com").apply { id = 1L }
        val site = Site("Obra 1", "Quito", user).apply { id = 1L }
        val response = SiteResponse(1L, "Obra 1", "Quito", listOf(), listOf(), UserResponse(1L, "Admin", "admin@obra.com"))

        `when`(siteRepository.findAll()).thenReturn(listOf(site))
        `when`(siteMapper.toResponseList(listOf(site))).thenReturn(listOf(response))

        val result = siteService.listSites()

        assertEquals(1, result.size)
        assertEquals("Obra 1", result[0].name)
    }

    @Test
    fun should_update_site_successfully() {
        val existingUser = User("User1", "u1@puce.edu.ec").apply { id = 1L }
        val newUser = User("User2", "u2@puce.edu.ec").apply { id = 2L }
        val site = Site("Obra Vieja", "Calle Falsa", existingUser).apply { id = 5L }

        val request = CreateSiteRequest("Obra Nueva", "Av Real", 2L)
        val response = SiteResponse(5L, "Obra Nueva", "Av Real", listOf(), listOf(), UserResponse(2L, "User2", "u2@puce.edu.ec"))

        `when`(siteRepository.findById(5L)).thenReturn(Optional.of(site))
        `when`(userRepository.findById(2L)).thenReturn(Optional.of(newUser))
        `when`(siteRepository.save(site)).thenReturn(site)
        `when`(siteMapper.toResponse(site)).thenReturn(response)

        val result = siteService.updateSite(5L, request)

        assertEquals("Obra Nueva", result.name)
        assertEquals("Av Real", result.address)
        assertEquals(2L, result.user.id)
    }

    @Test
    fun should_throw_exception_when_updating_nonexistent_site() {
        val request = CreateSiteRequest("Obra Nueva", "Av Real", 1L)
        `when`(siteRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<SiteNotFoundException> {
            siteService.updateSite(99L, request)
        }
    }

    @Test
    fun should_throw_exception_when_user_not_found_on_update() {
        val user = User("Admin", "admin@obra.com").apply { id = 1L }
        val site = Site("Antigua", "Calle Falsa", user).apply { id = 6L }

        val request = CreateSiteRequest("Nueva", "Av Real", 99L)

        `when`(siteRepository.findById(6L)).thenReturn(Optional.of(site))
        `when`(userRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<UserNotFoundException> {
            siteService.updateSite(6L, request)
        }
    }

    @Test
    fun should_delete_site() {
        val user = User("Admin", "admin@obra.com").apply { id = 1L }
        val site = Site("Obra", "Dirección", user).apply { id = 7L }

        `when`(siteRepository.findById(7L)).thenReturn(Optional.of(site))

        siteService.deleteSite(7L)

        verify(siteRepository).delete(site)
    }

    @Test
    fun should_throw_exception_when_deleting_nonexistent_site() {
        `when`(siteRepository.findById(999L)).thenReturn(Optional.empty())

        assertThrows<SiteNotFoundException> {
            siteService.deleteSite(999L)
        }
    }
}

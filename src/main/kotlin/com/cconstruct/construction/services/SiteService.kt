package com.cconstruct.construction.services

import com.cconstruct.construction.exceptions.*
import com.cconstruct.construction.mappers.SiteMapper
import com.cconstruct.construction.models.entities.Site
import com.cconstruct.construction.models.requests.CreateSiteRequest
import com.cconstruct.construction.models.responses.SiteResponse
import com.cconstruct.construction.repositories.SiteRepository
import com.cconstruct.construction.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class SiteService(
    private val siteRepository: SiteRepository,
    private val userRepository: UserRepository,
    private val siteMapper: SiteMapper
) {

    fun createSite(request: CreateSiteRequest): SiteResponse {
        if (siteRepository.existsByNameAndAddress(request.name, request.address)) {
            throw SiteAlreadyExistsException("Site with name '${request.name}' and address '${request.address}' already exists.")
        }

        val user = userRepository.findById(request.userId)
            .orElseThrow { UserNotFoundException("User with ID ${request.userId} not found.") }

        val site = Site(
            name = request.name,
            address = request.address,
            user = user
        )
        return siteMapper.toResponse(siteRepository.save(site))
    }

    fun getSiteById(id: Long): SiteResponse {
        val site = siteRepository.findById(id)
            .orElseThrow { SiteNotFoundException("Site with ID $id not found.") }
        return siteMapper.toResponse(site)
    }

    fun listSites(): List<SiteResponse> =
        siteMapper.toResponseList(siteRepository.findAll())

    fun updateSite(id: Long, request: CreateSiteRequest): SiteResponse {
        val site = siteRepository.findById(id)
            .orElseThrow { SiteNotFoundException("Site with ID $id not found.") }

        val user = userRepository.findById(request.userId)
            .orElseThrow { UserNotFoundException("User with ID ${request.userId} not found.") }

        site.name = request.name
        site.address = request.address
        site.user = user

        return siteMapper.toResponse(siteRepository.save(site))
    }


    fun deleteSite(id: Long) {
        val site = siteRepository.findById(id)
            .orElseThrow { SiteNotFoundException("Site with ID $id not found.") }
        siteRepository.delete(site)
    }
}

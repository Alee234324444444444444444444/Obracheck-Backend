package com.cconstruct.construction.controllers

import com.cconstruct.construction.constants.Routes
import com.cconstruct.construction.models.requests.CreateSiteRequest
import com.cconstruct.construction.models.responses.SiteResponse
import com.cconstruct.construction.services.SiteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Routes.SITES)
class SiteController(
    private val siteService: SiteService
) {

    @PostMapping
    fun createSite(@RequestBody request: CreateSiteRequest): SiteResponse =
        siteService.createSite(request)

    @GetMapping("/{id}")
    fun getSiteById(@PathVariable id: Long): SiteResponse =
        siteService.getSiteById(id)

    @GetMapping
    fun listSites(): List<SiteResponse> =
        siteService.listSites()

    @PutMapping("/{id}")
    fun updateSite(
        @PathVariable id: Long,
        @RequestBody request: CreateSiteRequest
    ): SiteResponse = siteService.updateSite(id, request)

    @DeleteMapping("/{id}")
    fun deleteSite(@PathVariable id: Long): ResponseEntity<Void> {
        siteService.deleteSite(id)
        return ResponseEntity.noContent().build()
    }
}

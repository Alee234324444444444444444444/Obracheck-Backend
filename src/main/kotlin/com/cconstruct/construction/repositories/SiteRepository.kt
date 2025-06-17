package com.cconstruct.construction.repositories

import com.cconstruct.construction.models.entities.Site
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SiteRepository : JpaRepository<Site, Long>{
    fun existsByNameAndAddress(name: String, address: String): Boolean
}
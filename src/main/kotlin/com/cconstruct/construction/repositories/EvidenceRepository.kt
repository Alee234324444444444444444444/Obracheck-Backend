package com.cconstruct.construction.repositories

import com.cconstruct.construction.models.entities.Evidence
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EvidenceRepository : JpaRepository<Evidence, Long>

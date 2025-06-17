package com.cconstruct.construction.repositories

import com.cconstruct.construction.models.entities.Progress
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgressRepository : JpaRepository<Progress, Long>

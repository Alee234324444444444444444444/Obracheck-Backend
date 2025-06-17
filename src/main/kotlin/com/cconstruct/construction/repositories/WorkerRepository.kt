package com.cconstruct.construction.repositories

import com.cconstruct.construction.models.entities.Worker
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkerRepository : JpaRepository<Worker, Long>{
    fun existsByCi(ci: String): Boolean
}

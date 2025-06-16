package com.cconstruct.construction.models.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "progresses")
class Progress(
    val description: String,
    val date: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "site_id")
    val site: Site,

    @ManyToOne
    @JoinColumn(name = "worker_id")
    val worker: Worker,

    @OneToMany(mappedBy = "progress", cascade = [CascadeType.ALL])
    val evidences: List<Evidence> = listOf()
) : BaseEntity()

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
data class Progress(
    var description: String,
    var date: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "site_id")
    var site: Site,

    @ManyToOne
    @JoinColumn(name = "worker_id")
    var worker: Worker,

    @OneToMany(mappedBy = "progress", cascade = [CascadeType.ALL])
    val evidences: List<Evidence> = listOf()
) : BaseEntity()

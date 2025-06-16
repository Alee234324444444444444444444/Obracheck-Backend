package com.cconstruct.construction.models.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "workers")
class Worker(
    val name: String,
    val role: String,
    val ci: String,

    @ManyToOne
    @JoinColumn(name = "site_id")
    val site: Site,

    @OneToMany(mappedBy = "worker", cascade = [CascadeType.ALL])
    val progresses: List<Progress> = listOf()
) : BaseEntity()

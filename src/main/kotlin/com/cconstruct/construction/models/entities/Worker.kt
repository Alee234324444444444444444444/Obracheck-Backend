package com.cconstruct.construction.models.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "workers")
data class Worker(
    var name: String,
    var role: String,
    val ci: String,

    @ManyToOne
    @JoinColumn(name = "site_id")
    var site: Site,

    @OneToMany(mappedBy = "worker", cascade = [CascadeType.ALL])
    val progresses: List<Progress> = listOf()
) : BaseEntity()

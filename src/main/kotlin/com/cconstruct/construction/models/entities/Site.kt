package com.cconstruct.construction.models.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "sites")
class Site(
    val name: String,
    val address: String,

    @OneToMany(mappedBy = "site", cascade = [CascadeType.ALL])
    val workers: List<Worker> = listOf(),

    @OneToMany(mappedBy = "site", cascade = [CascadeType.ALL])
    val progresses: List<Progress> = listOf()
) : BaseEntity()

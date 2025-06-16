package com.cconstruct.construction.models.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "sites")
class Site(
    val name: String,
    val address: String,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,


    @OneToMany(mappedBy = "site", cascade = [CascadeType.ALL], orphanRemoval = true)
    val workers: List<Worker> = listOf(),

    @OneToMany(mappedBy = "site", cascade = [CascadeType.ALL], orphanRemoval = true)
    val progresses: List<Progress> = listOf()
) : BaseEntity()

package com.cconstruct.construction.models.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class User(
    var name: String,

    @Column(unique = true)
    var email: String,
    ) : BaseEntity()
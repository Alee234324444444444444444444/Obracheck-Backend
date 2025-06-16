package com.cconstruct.construction.models.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    val name: String,

    @Column(unique = true)
    val email: String,


    val password: String
) : BaseEntity()
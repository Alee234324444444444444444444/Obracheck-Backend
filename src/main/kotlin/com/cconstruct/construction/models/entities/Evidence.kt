package com.cconstruct.construction.models.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "evidences")
data class Evidence(
    var fileName: String,

    @Lob
    @Column(columnDefinition = "BYTEA")
    var content: ByteArray,

    @ManyToOne
    @JoinColumn(name = "progress_id")
    var progress: Progress
) : BaseEntity()

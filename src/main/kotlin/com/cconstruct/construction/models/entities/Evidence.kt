package com.cconstruct.construction.models.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "evidences")
data class Evidence(
    @Column(name = "file_name", nullable = false)
    var fileName: String,

    @Column(name = "original_file_name", nullable = false)
    var originalFileName: String,

    @Column(name = "content_type", nullable = false)
    var contentType: String,

    @Column(name = "file_size", nullable = false)
    var fileSize: Long,

    @Lob
    @Column(name = "content", nullable = false)
    var content: ByteArray,

    @ManyToOne
    @JoinColumn(name = "progress_id")
    var progress: Progress,

    @Column(name = "upload_date", nullable = false)
    var uploadDate: LocalDateTime = LocalDateTime.now()
) : BaseEntity()
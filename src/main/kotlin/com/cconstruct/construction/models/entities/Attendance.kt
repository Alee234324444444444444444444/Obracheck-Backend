package com.cconstruct.construction.models.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "attendances")
data class Attendance(
    @ManyToOne

    var worker: Worker,

    @ManyToOne
    @JoinColumn(name = "site", nullable = false)
    var site: Site,

    @Column(nullable = false)
    var date: LocalDate = LocalDate.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: AttendanceStatus = AttendanceStatus.NA
) : BaseEntity()


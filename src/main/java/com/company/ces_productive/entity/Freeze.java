package com.company.ces_productive.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_FREEZE")
@Entity(name = "CES_Freeze")
public class Freeze {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "FREEZE_REASON", nullable = false)
    @NotNull
    private String freezeReason;

    @JoinColumn(name = "FREEZE_STUDENT_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Students freezeStudent;

    @Column(name = "FREEZE_START_DATE", nullable = false)
    @NotNull
    private LocalDate freezeStartDate;

    @NotNull
    @Column(name = "FREEZE_END_DATE", nullable = false)
    private LocalDate freezeEndDate;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public void setFreezeStudent(Students freezeStudent) {
        this.freezeStudent = freezeStudent;
    }

    public Students getFreezeStudent() {
        return freezeStudent;
    }

    public LocalDate getFreezeEndDate() {
        return freezeEndDate;
    }

    public void setFreezeEndDate(LocalDate freezeEndDate) {
        this.freezeEndDate = freezeEndDate;
    }

    public LocalDate getFreezeStartDate() {
        return freezeStartDate;
    }

    public void setFreezeStartDate(LocalDate freezeStartDate) {
        this.freezeStartDate = freezeStartDate;
    }

    public String getFreezeReason() {
        return freezeReason;
    }

    public void setFreezeReason(String freezeReason) {
        this.freezeReason = freezeReason;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
package com.company.ces_productive.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_STOPPED", indexes = {
        @Index(name = "IDX_CESSTOPPED_STOPPEDSTUDENT", columnList = "STOPPED_STUDENT_ID")
})
@Entity(name = "CES_Stopped")
public class Stopped {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "STOPPED_REASON")
    private String stoppedReason;

    @JoinColumn(name = "STOPPED_STUDENT_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Students stoppedStudent;

    @Column(name = "STOPPED_DATE", nullable = false)
    @NotNull
    private LocalDate stoppedDate;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public LocalDate getStoppedDate() {
        return stoppedDate;
    }

    public void setStoppedDate(LocalDate stoppedDate) {
        this.stoppedDate = stoppedDate;
    }

    public Students getStoppedStudent() {
        return stoppedStudent;
    }

    public void setStoppedStudent(Students stoppedStudent) {
        this.stoppedStudent = stoppedStudent;
    }

    public String getStoppedReason() {
        return stoppedReason;
    }

    public void setStoppedReason(String stoppedReason) {
        this.stoppedReason = stoppedReason;
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
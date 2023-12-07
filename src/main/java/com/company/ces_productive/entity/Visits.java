package com.company.ces_productive.entity;

import com.company.ces_productive.entity.courses.Courses;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_VISITS", indexes = {
        @Index(name = "IDX_CES_VISITS_VISIT_STUDENT", columnList = "VISIT_STUDENT_ID"),
        @Index(name = "IDX_CES_VISITS_VISIT_COURSE", columnList = "VISIT_COURSE_ID"),
        @Index(name = "IDX_CES_VISITS_VISIT_USER", columnList = "VISIT_USER_ID")
})
@Entity(name = "CES_Visits")
public class Visits {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "VISIT_STATUS", nullable = false)
    @NotNull
    private String visitStatus;

    @Column(name = "VISIT_START_DATE_TIME", nullable = false)
    @NotNull
    private LocalDateTime visitStartDateTime;

    @Column(name = "VISIT_END_DATE_TIME", nullable = false)
    @NotNull
    private LocalDateTime visitEndDateTime;

    @Column(name = "VISIT_AMOUNT", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal visitAmount;

    @JoinColumn(name = "VISIT_STUDENT_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Students visitStudent;

    @JoinColumn(name = "VISIT_COURSE_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Courses visitCourse;

    @JoinColumn(name = "VISIT_USER_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User visitUser;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public User getVisitUser() {
        return visitUser;
    }

    public void setVisitUser(User visitUser) {
        this.visitUser = visitUser;
    }

    public Courses getVisitCourse() {
        return visitCourse;
    }

    public void setVisitCourse(Courses visitCourse) {
        this.visitCourse = visitCourse;
    }

    public Students getVisitStudent() {
        return visitStudent;
    }

    public void setVisitStudent(Students visitStudent) {
        this.visitStudent = visitStudent;
    }

    public BigDecimal getVisitAmount() {
        return visitAmount;
    }

    public void setVisitAmount(BigDecimal visitAmount) {
        this.visitAmount = visitAmount;
    }

    public LocalDateTime getVisitEndDateTime() {
        return visitEndDateTime;
    }

    public void setVisitEndDateTime(LocalDateTime visitEndDateTime) {
        this.visitEndDateTime = visitEndDateTime;
    }

    public LocalDateTime getVisitStartDateTime() {
        return visitStartDateTime;
    }

    public void setVisitStartDateTime(LocalDateTime visitStartDateTime) {
        this.visitStartDateTime = visitStartDateTime;
    }

    public VisitStatus getVisitStatus() {
        return visitStatus == null ? null : VisitStatus.fromId(visitStatus);
    }

    public void setVisitStatus(VisitStatus visitStatus) {
        this.visitStatus = visitStatus == null ? null : visitStatus.getId();
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

    @InstanceName
    @DependsOnProperties({"visitCourse", "visitStudent"})
    public String getInstanceName() {
        return String.format("%s %s", visitCourse, visitStudent);
    }
}
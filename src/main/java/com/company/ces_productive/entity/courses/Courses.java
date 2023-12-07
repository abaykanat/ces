package com.company.ces_productive.entity.courses;

import com.company.ces_productive.entity.*;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_COURSES", indexes = {
        @Index(name = "IDX_CES_COURSES_COURSE_GROUP", columnList = "COURSE_GROUP_ID"),
        @Index(name = "IDX_CES_COURSES_COURSE_CABINET", columnList = "COURSE_CABINET_ID"),
        @Index(name = "IDX_CES_COURSES_COURSE_TEACHER", columnList = "COURSE_TEACHER_ID"),
        @Index(name = "IDX_CES_COURSES_COURSE_BRANCH", columnList = "COURSE_BRANCH_ID"),
        @Index(name = "IDX_CES_COURSES_COURSE_MANAGER", columnList = "COURSE_MANAGER_ID")
})
@Entity(name = "CES_Courses")
public class Courses {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "COURSE_NAME", nullable = false)
    @NotNull
    private String courseName;

    @Column(name = "COURSE_STATUS")
    private String courseStatus;

    @Column(name = "COURSE_COST")
    private BigDecimal courseCost;

    @Column(name = "COURSE_COUNT", precision = 19, scale = 2)
    private BigDecimal courseCount;

    @NotNull
    @Column(name = "COURSE_START_DATE", nullable = false)
    private LocalDateTime courseStartDate;

    @NotNull
    @Column(name = "COURSE_END_DATE", nullable = false)
    private LocalDateTime courseEndDate;

    @Column(name = "COURSE_START_TIME", nullable = false)
    @NotNull
    private LocalTime courseStartTime;

    @Column(name = "COURSE_END_TIME", nullable = false)
    @NotNull
    private LocalTime courseEndTime;

    @JoinColumn(name = "COURSE_GROUP_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Groups courseGroup;

    @JoinColumn(name = "COURSE_CABINET_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Cabinets courseCabinet;

    @JoinColumn(name = "COURSE_TEACHER_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User courseTeacher;

    @JoinColumn(name = "COURSE_MANAGER_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User courseManager;

    @JoinColumn(name = "COURSE_BRANCH_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Branches courseBranch;

    @Column(name = "COURSE_DESC", length = 500)
    private String courseDesc;

    @Column(name = "TYPE_", nullable = false)
    @NotNull
    private String type;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public String getCourseDesc() {
        return courseDesc;
    }

    public void setCourseDesc(String courseDesc) {
        this.courseDesc = courseDesc;
    }

    public void setCourseStatus(CourseStatus courseStatus) {
        this.courseStatus = courseStatus == null ? null : courseStatus.getId();
    }

    public CourseStatus getCourseStatus() {
        return courseStatus == null ? null : CourseStatus.fromId(courseStatus);
    }

    @JmixProperty
    @DependsOnProperties({"type"})
    public String getTypeStyle() {
        return Optional.ofNullable(getType())
                .map(CourseType::getStyleName)
                .orElse("");
    }

    public CourseType getType() {
        return type == null ? null : CourseType.fromId(type);
    }

    public void setType(CourseType type) {
        this.type = type == null ? null : type.getId();
    }
    public User getCourseManager() {
        return courseManager;
    }

    public void setCourseManager(User courseManager) {
        this.courseManager = courseManager;
    }

    public Branches getCourseBranch() {
        return courseBranch;
    }

    public void setCourseBranch(Branches courseBranch) {
        this.courseBranch = courseBranch;
    }

    public void setCourseCost(BigDecimal courseCost) {
        this.courseCost = courseCost;
    }

    public BigDecimal getCourseCost() {
        return courseCost;
    }

    public BigDecimal getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(BigDecimal courseCount) {
        this.courseCount = courseCount;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setCourseStartDate(LocalDateTime courseStartDate) {
        this.courseStartDate = courseStartDate;
    }

    public LocalDateTime getCourseStartDate() {
        return courseStartDate;
    }

    public void setCourseEndDate(LocalDateTime courseEndDate) {
        this.courseEndDate = courseEndDate;
    }

    public LocalDateTime getCourseEndDate() {
        return courseEndDate;
    }

    public User getCourseTeacher() {
        return courseTeacher;
    }

    public void setCourseTeacher(User courseTeacher) {
        this.courseTeacher = courseTeacher;
    }

    public Cabinets getCourseCabinet() {
        return courseCabinet;
    }

    public void setCourseCabinet(Cabinets courseCabinet) {
        this.courseCabinet = courseCabinet;
    }

    public LocalTime getCourseEndTime() {
        return courseEndTime;
    }

    public void setCourseEndTime(LocalTime courseEndTime) {
        this.courseEndTime = courseEndTime;
    }

    public LocalTime getCourseStartTime() {
        return courseStartTime;
    }

    public void setCourseStartTime(LocalTime courseStartTime) {
        this.courseStartTime = courseStartTime;
    }

    public Groups getCourseGroup() {
        return courseGroup;
    }

    public void setCourseGroup(Groups courseGroup) {
        this.courseGroup = courseGroup;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @InstanceName
    @DependsOnProperties({"courseName"})
    public String getInstanceName() {
        return String.format("%s", courseName);
    }
}
package com.company.ces_productive.entity;

import com.company.ces_productive.entity.courses.Courses;
import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_GROUPS", indexes = {
        @Index(name = "IDX_CES_GROUPS_GROUP_TEACHER", columnList = "GROUP_TEACHER_ID"),
        @Index(name = "IDX_CES_GROUPS_GROUP_BRANCH", columnList = "GROUP_BRANCH_ID")
})
@Entity(name = "CES_Groups")
public class Groups {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "GROUP_NAME", nullable = false, length = 30)
    @NotNull
    private String groupName;

    @Column(name = "GROUP_STATUS", nullable = false)
    private String groupStatus;

    @NotNull
    @Column(name = "GROUP_COST", nullable = false)
    private BigDecimal groupCost;

    @Column(name = "GROUP_COUNT", precision = 19, scale = 2)
    private BigDecimal groupCount;

    @JoinTable(name = "CES_GROUPS_STUDENTS_LINK",
            joinColumns = @JoinColumn(name = "GROUPS_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "STUDENTS_ID", referencedColumnName = "ID"))
    @ManyToMany
    private List<Students> groupStudents;

    @OneToMany(mappedBy = "courseGroup")
    private List<Courses> groupCourse;

    @JoinColumn(name = "GROUP_TYPE_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Direction groupDirection;

    @JoinColumn(name = "GROUP_TEACHER_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User groupTeacher;

    @JoinColumn(name = "GROUP_BRANCH_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Branches groupBranch;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @DeletedBy
    @Column(name = "DELETED_BY")
    private String deletedBy;

    @DeletedDate
    @Column(name = "DELETED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedDate;

    public BigDecimal getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(BigDecimal groupCount) {
        this.groupCount = groupCount;
    }

    public void setGroupStatus(GroupStatus groupStatus) {
        this.groupStatus = groupStatus == null ? null : groupStatus.getId();
    }

    public GroupStatus getGroupStatus() {
        return groupStatus == null ? null : GroupStatus.fromId(groupStatus);
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public void setGroupCost(BigDecimal groupCost) {
        this.groupCost = groupCost;
    }

    public BigDecimal getGroupCost() {
        return groupCost;
    }

    public Branches getGroupBranch() {
        return groupBranch;
    }

    public void setGroupBranch(Branches groupBranch) {
        this.groupBranch = groupBranch;
    }

    public void setGroupDirection(Direction groupDirection) {
        this.groupDirection = groupDirection;
    }

    public Direction getGroupDirection() {
        return groupDirection;
    }

    public List<Courses> getGroupCourse() {
        return groupCourse;
    }

    public void setGroupCourse(List<Courses> groupCourse) {
        this.groupCourse = groupCourse;
    }

    public User getGroupTeacher() {
        return groupTeacher;
    }

    public void setGroupTeacher(User groupTeacher) {
        this.groupTeacher = groupTeacher;
    }

    public List<Students> getGroupStudents() {
        return groupStudents;
    }

    public void setGroupStudents(List<Students> groupStudents) {
        this.groupStudents = groupStudents;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
    @DependsOnProperties({"groupName"})
    public String getInstanceName() {
        return String.format("%s", groupName);
    }
}
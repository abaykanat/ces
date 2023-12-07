package com.company.ces_productive.entity;

import com.company.ces_productive.entity.courses.CourseType;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_DIRECTION", indexes = {
        @Index(name = "IDX_CES_DIRECTION_DIRECTION_BRANCH", columnList = "DIRECTION_BRANCH_ID")
})
@Entity(name = "CES_Direction")
public class Direction {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "DIRECTION_NAME", nullable = false, length = 30)
    @NotNull
    private String directionName;

    @Column(name = "DIRECTION_TYPE", nullable = false)
    @NotNull
    private String directionType;

    @NotNull
    @Column(name = "DIRECTION_DESC", nullable = false)
    private String directionDesc;

    @NotNull
    @Column(name = "DIRECTION_MIN_COST", nullable = false)
    private BigDecimal directionMinCost;

    @Column(name = "DIRECTION_COUNT", precision = 19, scale = 2)
    private BigDecimal directionCount;

    @Column(name = "DIRECTION_DURATION", nullable = false)
    @NotNull
    private Long directionDuration;

    @OneToMany(mappedBy = "groupDirection")
    private List<Groups> directionGroups;

    @NotNull
    @JoinColumn(name = "DIRECTION_BRANCH_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Branches directionBranch;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public List<Groups> getDirectionGroups() {
        return directionGroups;
    }

    public void setDirectionGroups(List<Groups> directionGroups) {
        this.directionGroups = directionGroups;
    }

    public BigDecimal getDirectionCount() {
        return directionCount;
    }

    public void setDirectionCount(BigDecimal directionCount) {
        this.directionCount = directionCount;
    }

    public Long getDirectionDuration() {
        return directionDuration;
    }

    public void setDirectionDuration(Long directionDuration) {
        this.directionDuration = directionDuration;
    }

    public Branches getDirectionBranch() {
        return directionBranch;
    }

    public void setDirectionBranch(Branches directionBranch) {
        this.directionBranch = directionBranch;
    }

    public void setDirectionType(CourseType directionType) {
        this.directionType = directionType == null ? null : directionType.getId();
    }

    public CourseType getDirectionType() {
        return directionType == null ? null : CourseType.fromId(directionType);
    }

    public void setDirectionMinCost(BigDecimal directionMinCost) {
        this.directionMinCost = directionMinCost;
    }

    public BigDecimal getDirectionMinCost() {
        return directionMinCost;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getDirectionDesc() {
        return directionDesc;
    }

    public void setDirectionDesc(String directionDesc) {
        this.directionDesc = directionDesc;
    }

    public String getDirectionName() {
        return directionName;
    }

    public void setDirectionName(String directionName) {
        this.directionName = directionName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @InstanceName
    @DependsOnProperties({"directionName"})
    public String getInstanceName() {
        return String.format("%s", directionName);
    }
}
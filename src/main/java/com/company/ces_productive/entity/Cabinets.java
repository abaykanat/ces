package com.company.ces_productive.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_CABINETS", indexes = {
        @Index(name = "IDX_CESCABINETS_CABINETBRANCH", columnList = "CABINET_BRANCH_ID")
})
@Entity(name = "CES_Cabinets")
public class Cabinets {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "CABINET_NAME", nullable = false)
    @NotNull
    private String cabinetName;

    @NotNull
    @Column(name = "CABINET_AREA", nullable = false)
    private Long cabinetArea;

    @JoinColumn(name = "CABINET_BRANCH_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Branches cabinetBranch;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public void setCabinetArea(Long cabinetArea) {
        this.cabinetArea = cabinetArea;
    }

    public Long getCabinetArea() {
        return cabinetArea;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Branches getCabinetBranch() {
        return cabinetBranch;
    }

    public void setCabinetBranch(Branches cabinetBranch) {
        this.cabinetBranch = cabinetBranch;
    }

    public String getCabinetName() {
        return cabinetName;
    }

    public void setCabinetName(String cabinetName) {
        this.cabinetName = cabinetName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @InstanceName
    @DependsOnProperties({"cabinetName"})
    public String getInstanceName() {
        return String.format("%s", cabinetName);
    }
}
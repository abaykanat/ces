package com.company.ces_productive.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_BRANCHES")
@Entity(name = "CES_Branches")
public class Branches {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "BRANCH_NAME", nullable = false)
    @NotNull
    private String branchName;

    @Column(name = "BRANCH_CODE", nullable = false, length = 5)
    @NotNull
    private String branchCode;

    @Column(name = "BRANCH_CITY", nullable = false)
    @NotNull
    private String branchCity;

    @NotNull
    @Column(name = "BRANCH_CITY_KZ", nullable = false)
    private String branchCityKz;

    @Column(name = "BRANCH_STREET", nullable = false)
    @NotNull
    private String branchStreet;

    @Column(name = "BRANCH_STREET_KZ", nullable = false)
    @NotNull
    private String branchStreetKz;

    @Column(name = "BRANCH_TEL_NUMBER", nullable = false)
    @NotNull
    private String branchTelNumber;

    @Column(name = "BRANCH_BEGIN_DATE", nullable = false)
    @NotNull
    private LocalDate branchBeginDate;

    @Column(name = "BRANCH_LEGAL_ORG")
    private String branchLegalOrg;

    @Column(name = "BRANCH_LEGAL_BIN")
    private String branchLegalBIN;

    @Column(name = "BRANCH_LEGAL_INFO_KZ", length = 1000)
    private String branchLegalInfoKz;

    @Column(name = "BRANCH_LEGAL_INFO", length = 1000)
    private String branchLegalInfo;

    @Column(name = "BRANCH_STATUS", nullable = false)
    @NotNull
    private String branchStatus;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public String getBranchStreetKz() {
        return branchStreetKz;
    }

    public void setBranchStreetKz(String branchStreetKz) {
        this.branchStreetKz = branchStreetKz;
    }

    public String getBranchCityKz() {
        return branchCityKz;
    }

    public void setBranchCityKz(String branchCityKz) {
        this.branchCityKz = branchCityKz;
    }

    public String getBranchLegalBIN() {
        return branchLegalBIN;
    }

    public void setBranchLegalBIN(String branchLegalBIN) {
        this.branchLegalBIN = branchLegalBIN;
    }

    public String getBranchLegalOrg() {
        return branchLegalOrg;
    }

    public void setBranchLegalOrg(String branchLegalOrg) {
        this.branchLegalOrg = branchLegalOrg;
    }

    public String getBranchLegalInfoKz() {
        return branchLegalInfoKz;
    }

    public void setBranchLegalInfoKz(String branchLegalInfoKz) {
        this.branchLegalInfoKz = branchLegalInfoKz;
    }

    public String getBranchLegalInfo() {
        return branchLegalInfo;
    }

    public void setBranchLegalInfo(String branchLegalInfo) {
        this.branchLegalInfo = branchLegalInfo;
    }

    public void setBranchStatus(BranchStatus branchStatus) {
        this.branchStatus = branchStatus == null ? null : branchStatus.getId();
    }

    public BranchStatus getBranchStatus() {
        return branchStatus == null ? null : BranchStatus.fromId(branchStatus);
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDate getBranchBeginDate() {
        return branchBeginDate;
    }

    public void setBranchBeginDate(LocalDate branchBeginDate) {
        this.branchBeginDate = branchBeginDate;
    }

    public String getBranchTelNumber() {
        return branchTelNumber;
    }

    public void setBranchTelNumber(String branchTelNumber) {
        this.branchTelNumber = branchTelNumber;
    }

    public String getBranchStreet() {
        return branchStreet;
    }

    public void setBranchStreet(String branchStreet) {
        this.branchStreet = branchStreet;
    }

    public String getBranchCity() {
        return branchCity;
    }

    public void setBranchCity(String branchCity) {
        this.branchCity = branchCity;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @InstanceName
    @DependsOnProperties({"branchCode", "branchName"})
    public String getInstanceName() {
        return String.format("%s %s", branchCode, branchName);
    }
}
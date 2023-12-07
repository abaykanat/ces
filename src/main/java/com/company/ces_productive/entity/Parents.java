package com.company.ces_productive.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_PARENTS", indexes = {
        @Index(name = "IDX_CES_PARENTS_PARENT_BRANCH", columnList = "PARENT_BRANCH_ID"),
        @Index(name = "IDX_CES_PARENTS_PARENT_SEC_BRANCH", columnList = "PARENT_SEC_BRANCH_ID")
}, uniqueConstraints = {
        @UniqueConstraint(name = "IDX_CES_PARENTS_UNQ", columnNames = {"PARENT_IIN"}),
        @UniqueConstraint(name = "IDX_CES_PARENTS_UNQ_1", columnNames = {"PARENT_MOBILE_NUMBER"})
})
@Entity(name = "CES_Parents")
public class Parents {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "PARENT_IIN", nullable = false, length = 12)
    @NotNull
    private String parentIIN;

    @Column(name = "PARENT_FIRST_NAME", nullable = false)
    @NotNull
    private String parentFirstName;

    @NotNull
    @Column(name = "PARENT_LAST_NAME", nullable = false)
    private String parentLastName;

    @Column(name = "PARENT_MIDDLE_NAME")
    private String parentMiddleName;

    @Column(name = "PARENT_MOBILE_NUMBER", nullable = false)
    @NotNull
    private String parentMobileNumber;

    @Email(message = "{msg://com.company.ces_productive.entity/Parents.parentEmail.validation.Email}")
    @Column(name = "PARENT_EMAIL")
    private String parentEmail;

    @Column(name = "PARENT_ADDRESS")
    private String parentAddress;

    @JoinColumn(name = "PARENT_BRANCH_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Branches parentBranch;

    @JoinColumn(name = "PARENT_SEC_BRANCH_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Branches parentSecBranch;

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public Branches getParentSecBranch() {
        return parentSecBranch;
    }

    public void setParentSecBranch(Branches parentSecBranch) {
        this.parentSecBranch = parentSecBranch;
    }

    public String getParentIIN() {
        return parentIIN;
    }

    public void setParentIIN(String parentIIN) {
        this.parentIIN = parentIIN;
    }

    public Branches getParentBranch() {
        return parentBranch;
    }

    public void setParentBranch(Branches parentBranch) {
        this.parentBranch = parentBranch;
    }

    public String getParentAddress() {
        return parentAddress;
    }

    public void setParentAddress(String parentAddress) {
        this.parentAddress = parentAddress;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public String getParentMobileNumber() {
        return parentMobileNumber;
    }

    public void setParentMobileNumber(String parentMobileNumber) {
        this.parentMobileNumber = parentMobileNumber;
    }

    public String getParentMiddleName() {
        return parentMiddleName;
    }

    public void setParentMiddleName(String parentMiddleName) {
        this.parentMiddleName = parentMiddleName;
    }

    public String getParentLastName() {
        return parentLastName;
    }

    public void setParentLastName(String parentLastName) {
        this.parentLastName = parentLastName;
    }

    public String getParentFirstName() {
        return parentFirstName;
    }

    public void setParentFirstName(String parentFirstName) {
        this.parentFirstName = parentFirstName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @InstanceName
    @DependsOnProperties({"parentFirstName", "parentLastName"})
    public String getInstanceName() {
        return String.format("%s %s", parentFirstName, parentLastName);
    }
}
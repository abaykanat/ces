package com.company.ces_productive.entity;

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
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_PAYMENTS", indexes = {
        @Index(name = "IDX_CES_PAYMENTS_PAY_STUDENT", columnList = "PAY_STUDENT_ID"),
        @Index(name = "IDX_CES_PAYMENTS_PAY_ORDER", columnList = "PAY_ORDER_ID"),
        @Index(name = "IDX_CES_PAYMENTS_PAY_BRANCH", columnList = "PAY_BRANCH_ID"),
        @Index(name = "IDX_CES_PAYMENTS_PAY_USER", columnList = "PAY_USER_ID")
})
@Entity(name = "CES_Payments")
public class Payments {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "PAY_NUMBER", nullable = false, length = 30)
    @NotNull
    private String payNumber;

    @NotNull
    @Column(name = "PAY_AMOUNT", nullable = false, precision = 19, scale = 2)
    private BigDecimal payAmount;

    @NotNull
    @Column(name = "PAY_DATE_TIME", nullable = false)
    private LocalDate payDateTime;

    @Column(name = "PAY_PURPOSE", nullable = false)
    @NotNull
    private String payPurpose;

    @Column(name = "PAY_MODE", nullable = false)
    @NotNull
    private String payMode;

    @JoinColumn(name = "PAY_STUDENT_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Students payStudent;

    @JoinColumn(name = "PAY_ORDER_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Orders payOrder;

    @JoinColumn(name = "PAY_USER_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User payUser;

    @JoinColumn(name = "PAY_BRANCH_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Branches payBranch;

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

    public void setPayDateTime(LocalDate payDateTime) {
        this.payDateTime = payDateTime;
    }

    public LocalDate getPayDateTime() {
        return payDateTime;
    }

    public User getPayUser() {
        return payUser;
    }

    public void setPayUser(User payUser) {
        this.payUser = payUser;
    }

    public Branches getPayBranch() {
        return payBranch;
    }

    public void setPayBranch(Branches payBranch) {
        this.payBranch = payBranch;
    }

    public Orders getPayOrder() {
        return payOrder;
    }

    public void setPayOrder(Orders payOrder) {
        this.payOrder = payOrder;
    }

    public Students getPayStudent() {
        return payStudent;
    }

    public void setPayStudent(Students payStudent) {
        this.payStudent = payStudent;
    }

    public PaymentMode getPayMode() {
        return payMode == null ? null : PaymentMode.fromId(payMode);
    }

    public void setPayMode(PaymentMode payMode) {
        this.payMode = payMode == null ? null : payMode.getId();
    }

    public OrderPurpose getPayPurpose() {
        return payPurpose == null ? null : OrderPurpose.fromId(payPurpose);
    }

    public void setPayPurpose(OrderPurpose payPurpose) {
        this.payPurpose = payPurpose == null ? null : payPurpose.getId();
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public String getPayNumber() {
        return payNumber;
    }

    public void setPayNumber(String payNumber) {
        this.payNumber = payNumber;
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
    @DependsOnProperties({"payNumber", "payDateTime"})
    public String getInstanceName() {
        return String.format("%s %s", payNumber, payDateTime);
    }
}
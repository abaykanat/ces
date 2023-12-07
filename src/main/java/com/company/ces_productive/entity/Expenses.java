package com.company.ces_productive.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
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
@Table(name = "CES_EXPENSES", indexes = {
        @Index(name = "IDX_CES_EXPENSES_EXPENSE_REASON", columnList = "EXPENSE_REASON_ID"),
        @Index(name = "IDX_CES_EXPENSES_EXPENSES_USER", columnList = "EXPENSES_USER_ID"),
        @Index(name = "IDX_CES_EXPENSES_EXPENSES_BRANCH", columnList = "EXPENSES_BRANCH_ID")
})
@Entity(name = "CES_Expenses")
public class Expenses {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "EXP_NUMBER", nullable = false, length = 30)
    @NotNull
    private String expensesNumber;

    @Column(name = "EXPENSE_AMOUNT", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal expensesAmount;

    @Column(name = "EXPENSES_NAME", nullable = false)
    @NotNull
    private String expensesDesc;

    @JoinColumn(name = "EXPENSE_REASON_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ExpenseReason expensesReason;

    @NotNull
    @Column(name = "EXPENSE_DATE", nullable = false)
    private LocalDate expensesDate;

    @NotNull
    @JoinColumn(name = "EXPENSES_USER_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User expensesUser;

    @NotNull
    @JoinColumn(name = "EXPENSES_BRANCH_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Branches expensesBranch;

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

    public String getExpensesNumber() {
        return expensesNumber;
    }

    public void setExpensesNumber(String expensesNumber) {
        this.expensesNumber = expensesNumber;
    }

    public void setExpensesDate(LocalDate expensesDate) {
        this.expensesDate = expensesDate;
    }

    public LocalDate getExpensesDate() {
        return expensesDate;
    }

    public void setExpensesBranch(Branches expensesBranch) {
        this.expensesBranch = expensesBranch;
    }

    public Branches getExpensesBranch() {
        return expensesBranch;
    }

    public void setExpensesUser(User expensesUser) {
        this.expensesUser = expensesUser;
    }

    public User getExpensesUser() {
        return expensesUser;
    }

    public BigDecimal getExpensesAmount() {
        return expensesAmount;
    }

    public void setExpensesAmount(BigDecimal expensesAmount) {
        this.expensesAmount = expensesAmount;
    }

    public ExpenseReason getExpensesReason() {
        return expensesReason;
    }

    public void setExpensesReason(ExpenseReason expensesReason) {
        this.expensesReason = expensesReason;
    }

    public String getExpensesDesc() {
        return expensesDesc;
    }

    public void setExpensesDesc(String expensesDesc) {
        this.expensesDesc = expensesDesc;
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
}
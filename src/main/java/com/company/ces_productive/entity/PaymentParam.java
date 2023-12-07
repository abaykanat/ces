package com.company.ces_productive.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_PAYMENT_PARAM", indexes = {
        @Index(name = "IDX_CES_PAYMENT_PARAM_PAY_PARAM_STUDENT", columnList = "PAY_PARAM_STUDENT_ID"),
        @Index(name = "IDX_CES_PAYMENT_PARAM_PAY_PARAM_GROUPS", columnList = "PAY_PARAM_GROUPS_ID"),
        @Index(name = "IDX_CES_PAYMENT_PARAM_PAY_PARAM_DISCOUNT", columnList = "PAY_PARAM_DISCOUNT_ID")
})
@Entity(name = "CES_PaymentParam")
public class PaymentParam {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @JoinColumn(name = "PAY_PARAM_STUDENT_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Students payParamStudent;

    @JoinColumn(name = "PAY_PARAM_GROUPS_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Groups payParamGroups;

    @Column(name = "PAY_PARAM_PAY_DAY", nullable = false)
    @NotNull
    private LocalDate payParamPayDay;

    @JoinColumn(name = "PAY_PARAM_DISCOUNT_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private DiscountReason payParamDiscountReason;

    @Column(name = "PAY_PARAM_DISCONT_AMOUNT", precision = 19, scale = 2)
    private BigDecimal payParamDiscontAmount;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public void setPayParamDiscountReason(DiscountReason payParamDiscountReason) {
        this.payParamDiscountReason = payParamDiscountReason;
    }

    public DiscountReason getPayParamDiscountReason() {
        return payParamDiscountReason;
    }

    public BigDecimal getPayParamDiscontAmount() {
        return payParamDiscontAmount;
    }

    public void setPayParamDiscontAmount(BigDecimal payParamDiscontAmount) {
        this.payParamDiscontAmount = payParamDiscontAmount;
    }

    public LocalDate getPayParamPayDay() {
        return payParamPayDay;
    }

    public void setPayParamPayDay(LocalDate payParamPayDay) {
        this.payParamPayDay = payParamPayDay;
    }

    public DiscountReason getPayParamDiscount() {
        return payParamDiscountReason;
    }

    public void setPayParamDiscount(DiscountReason payParamDiscount) {
        this.payParamDiscountReason = payParamDiscount;
    }

    public Groups getPayParamGroups() {
        return payParamGroups;
    }

    public void setPayParamGroups(Groups payParamGroups) {
        this.payParamGroups = payParamGroups;
    }

    public Students getPayParamStudent() {
        return payParamStudent;
    }

    public void setPayParamStudent(Students payParamStudent) {
        this.payParamStudent = payParamStudent;
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
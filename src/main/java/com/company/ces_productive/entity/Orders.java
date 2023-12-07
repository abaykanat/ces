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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_ORDERS", indexes = {
        @Index(name = "IDX_CES_ORDERS_ORDER_BRANCH", columnList = "ORDER_BRANCH_ID"),
        @Index(name = "IDX_CES_ORDERS_ORDER_STUDENT", columnList = "ORDER_STUDENT_ID"),
        @Index(name = "IDX_CES_ORDERS_ORDER_BOOK", columnList = "ORDER_BOOK_ID"),
        @Index(name = "IDX_CES_ORDERS_ORDER_GROUP", columnList = "ORDER_GROUP_ID")
})
@Entity(name = "CES_Orders")
public class Orders {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotNull
    @Column(name = "ORDER_NUMBER", nullable = false, length = 30)
    private String orderNumber;

    @Column(name = "ORDER_DATE_TIME", nullable = false)
    @NotNull
    private LocalDateTime orderDateTime;

    @Column(name = "ORDER_AMOUNT", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal orderAmount;

    @Column(name = "ORDER_PART_AMOUNT", precision = 19, scale = 2)
    private BigDecimal orderPartAmount;

    @Column(name = "ORDER_PERIOD_END")
    private LocalDate orderPeriodEnd;

    @JoinColumn(name = "ORDER_STUDENT_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Students orderStudent;

    @Column(name = "ORDER_STATUS", nullable = false)
    @NotNull
    private String orderStatus;

    @Column(name = "ORDER_PURPOSE", nullable = false)
    @NotNull
    private String orderPurpose;

    @OneToMany(mappedBy = "payOrder")
    private List<Payments> orderPayment;

    @JoinColumn(name = "ORDER_GROUP_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Groups orderGroup;

    @JoinColumn(name = "ORDER_BOOK_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Books orderBook;

    @JoinColumn(name = "ORDER_BRANCH_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Branches orderBranch;

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

    public List<Payments> getOrderPayment() {
        return orderPayment;
    }

    public void setOrderPayment(List<Payments> orderPayment) {
        this.orderPayment = orderPayment;
    }

    public BigDecimal getOrderPartAmount() {
        return orderPartAmount;
    }

    public void setOrderPartAmount(BigDecimal orderPartAmount) {
        this.orderPartAmount = orderPartAmount;
    }

    public Groups getOrderGroup() {
        return orderGroup;
    }

    public void setOrderGroup(Groups orderGroup) {
        this.orderGroup = orderGroup;
    }

    public Books getOrderBook() {
        return orderBook;
    }

    public void setOrderBook(Books orderBook) {
        this.orderBook = orderBook;
    }

    public OrderPurpose getOrderPurpose() {
        return orderPurpose == null ? null : OrderPurpose.fromId(orderPurpose);
    }

    public void setOrderPurpose(OrderPurpose orderPurpose) {
        this.orderPurpose = orderPurpose == null ? null : orderPurpose.getId();
    }

    public OrderStatus getOrderStatus() {
        return orderStatus == null ? null : OrderStatus.fromId(orderStatus);
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus == null ? null : orderStatus.getId();
    }

    public Students getOrderStudent() {
        return orderStudent;
    }

    public void setOrderStudent(Students orderStudent) {
        this.orderStudent = orderStudent;
    }

    public Branches getOrderBranch() {
        return orderBranch;
    }

    public void setOrderBranch(Branches orderBranch) {
        this.orderBranch = orderBranch;
    }

    public LocalDate getOrderPeriodEnd() {
        return orderPeriodEnd;
    }

    public void setOrderPeriodEnd(LocalDate orderPeriodEnd) {
        this.orderPeriodEnd = orderPeriodEnd;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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
    @DependsOnProperties({"orderNumber"})
    public String getInstanceName() {
        return String.format("%s", orderNumber);
    }
}
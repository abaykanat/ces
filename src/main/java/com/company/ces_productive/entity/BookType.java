package com.company.ces_productive.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "CES_BOOK_TYPE")
@JmixEntity
@Entity(name = "CES_BookType")
public class BookType {

    @Column(name = "BOOK_TYPE_NAME", nullable = false, length = 50)
    @NotNull
    private String bookTypeName;

    @NotNull
    @Column(name = "BOOK_TYPE_CODE", nullable = false, length = 5)
    private String bookTypeCode;

    @Column(name = "BOOK_TYPE_DESC", nullable = false)
    @NotNull
    private String bookTypeDesc;

    @Column(name = "BOOK_TYPE_COST", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal bookTypeCost;

    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

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

    public String getBookTypeCode() {
        return bookTypeCode;
    }

    public void setBookTypeCode(String bookTypeCode) {
        this.bookTypeCode = bookTypeCode;
    }

    public String getBookTypeDesc() {
        return bookTypeDesc;
    }

    public void setBookTypeDesc(String bookTypeDesc) {
        this.bookTypeDesc = bookTypeDesc;
    }

    public BigDecimal getBookTypeCost() {
        return bookTypeCost;
    }

    public void setBookTypeCost(BigDecimal bookTypeCost) {
        this.bookTypeCost = bookTypeCost;
    }

    public String getBookTypeName() {
        return bookTypeName;
    }

    public void setBookTypeName(String bookTypeName) {
        this.bookTypeName = bookTypeName;
    }

    @InstanceName
    @DependsOnProperties({"bookTypeName"})
    public String getInstanceName() {
        return String.format("%s", bookTypeName);
    }
}
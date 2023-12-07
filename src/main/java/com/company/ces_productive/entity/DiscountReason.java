package com.company.ces_productive.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_DISCOUNT_REASON")
@Entity(name = "CES_DiscountReason")
public class DiscountReason {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "DISCONT_REASON_NAME", nullable = false, length = 20)
    @NotNull
    private String discontReasonName;

    @Column(name = "DISCONT_REASON_DESC", nullable = false)
    @NotNull
    private String discontReasonDesc;

    @Column(name = "DISCONT_REASON_DOC", nullable = false)
    @NotNull
    private Boolean discontReasonDoc = false;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public Boolean getDiscontReasonDoc() {
        return discontReasonDoc;
    }

    public void setDiscontReasonDoc(Boolean discontReasonDoc) {
        this.discontReasonDoc = discontReasonDoc;
    }

    public String getDiscontReasonDesc() {
        return discontReasonDesc;
    }

    public void setDiscontReasonDesc(String discontReasonDesc) {
        this.discontReasonDesc = discontReasonDesc;
    }

    public String getDiscontReasonName() {
        return discontReasonName;
    }

    public void setDiscontReasonName(String discontReasonName) {
        this.discontReasonName = discontReasonName;
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
    @DependsOnProperties({"discontReasonName"})
    public String getInstanceName() {
        return String.format("%s", discontReasonName);
    }
}
package com.company.ces_productive.entity;

import io.jmix.core.FileRef;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_DOCUMENTATION")
@Entity(name = "CES_Documentation")
public class Documentation {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotNull
    @Column(name = "DOCUMENT", nullable = false, length = 1024)
    private FileRef docUserManual;

    @Column(name = "ROLE_DOC", nullable = false)
    @NotNull
    private String docRole;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public void setDocUserManual(FileRef docUserManual) {
        this.docUserManual = docUserManual;
    }

    public FileRef getDocUserManual() {
        return docUserManual;
    }

    public void setDocRole(String docRole) {
        this.docRole = docRole;
    }

    public String getDocRole() {
        return docRole;
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
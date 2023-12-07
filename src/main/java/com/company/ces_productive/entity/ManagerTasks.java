package com.company.ces_productive.entity;

import io.jmix.core.entity.annotation.CaseConversion;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_MANAGER_TASKS")
@Entity(name = "CES_ManagerTasks")
public class ManagerTasks {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @CaseConversion
    @Column(name = "TASK_NAME", nullable = false, length = 10)
    @NotNull
    private String taskName;

    @Column(name = "TASK_DESCRIPTION", nullable = false, length = 150)
    @NotNull
    private String taskDescription;

    @NotNull
    @Column(name = "TASK_ROLE", nullable = false)
    private String taskRole;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public void setTaskRole(String taskRole) {
        this.taskRole = taskRole;
    }

    public String getTaskRole() {
        return taskRole;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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
    @DependsOnProperties({"taskName"})
    public String getInstanceName() {
        return String.format("%s", taskName);
    }
}
package com.company.ces_productive.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "CES_PASSWORD_RESET_TOKEN", indexes = {
        @Index(name = "IDX_CES_PASSWORD_RESET_TOKEN_USER_ID", columnList = "USER_ID_ID")
})
@Entity(name = "CES_PasswordResetToken")
public class PasswordResetToken {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @JoinColumn(name = "USER_ID_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User userId;

    @Column(name = "TOKEN", nullable = false)
    @NotNull
    private String token;

    @Column(name = "EXPIRATION_DATE", nullable = false)
    @NotNull
    private LocalDateTime expirationDate;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
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
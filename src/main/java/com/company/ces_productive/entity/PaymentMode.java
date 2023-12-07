package com.company.ces_productive.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum PaymentMode implements EnumClass<String> {

    CASH("CASH"),
    KASPI_QR("KASPI_QR"),
    KASPI_TRANSFER("KASPI_TRANSFER");

    private String id;

    PaymentMode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static PaymentMode fromId(String id) {
        for (PaymentMode at : PaymentMode.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
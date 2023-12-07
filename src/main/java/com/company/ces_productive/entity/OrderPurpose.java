package com.company.ces_productive.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum OrderPurpose implements EnumClass<String> {

    SUBSCRIPTION("SUBSCRIPTION"),
    BOOK("BOOK");

    private String id;

    OrderPurpose(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static OrderPurpose fromId(String id) {
        for (OrderPurpose at : OrderPurpose.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
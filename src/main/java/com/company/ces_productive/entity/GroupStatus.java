package com.company.ces_productive.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum GroupStatus implements EnumClass<String> {

    OPEN("OPEN"),
    FULL("FULL"),
    OVERDUE("OVERDUE");

    private String id;

    GroupStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static GroupStatus fromId(String id) {
        for (GroupStatus at : GroupStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
package com.company.ces_productive.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum VisitStatus implements EnumClass<String> {

    ATTENDED("ATTENDED"),
    ABSENT("ABSENT"),
    NOT_DEFINED("NOT_DEFINED");

    private String id;

    VisitStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static VisitStatus fromId(String id) {
        for (VisitStatus at : VisitStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
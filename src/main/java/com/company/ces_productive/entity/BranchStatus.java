package com.company.ces_productive.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum BranchStatus implements EnumClass<String> {

    OPEN("OPEN"),
    CLOSE("CLOSE");

    private String id;

    BranchStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static BranchStatus fromId(String id) {
        for (BranchStatus at : BranchStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
package com.company.ces_productive.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum StudentSex implements EnumClass<String> {

    BOY("BOY"),
    GIRL("GIRL");

    private String id;

    StudentSex(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static StudentSex fromId(String id) {
        for (StudentSex at : StudentSex.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
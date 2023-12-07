package com.company.ces_productive.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum StudentStatus implements EnumClass<String> {

    NEW("NEW"),
    STUDY("STUDY"),
    FREEZE("FREEZE"),
    STOPPED("STOPPED");

    private String id;

    StudentStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static StudentStatus fromId(String id) {
        for (StudentStatus at : StudentStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
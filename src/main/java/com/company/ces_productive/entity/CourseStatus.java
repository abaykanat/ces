package com.company.ces_productive.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum CourseStatus implements EnumClass<String> {

    NEW("NEW"),
    HELD("HELD"),
    NOT_DONE("NOT_DONE");

    private String id;

    CourseStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static CourseStatus fromId(String id) {
        for (CourseStatus at : CourseStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
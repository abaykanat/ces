package com.company.ces_productive.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum CourseMode implements EnumClass<String> {

    REPEAT("REPEAT"),
    SINGLE("SINGLE");

    private String id;

    CourseMode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static CourseMode fromId(String id) {
        for (CourseMode at : CourseMode.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
package com.company.ces_productive.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum StudentLevel implements EnumClass<String> {

    BEGINNER("A0"),
    ELEMENTARY("A1"),
    PRE_INTERMEDIATE("A2"),
    INTERMEDIATE("B1"),
    UPPER_INTERMEDIATE("B2"),
    ADVANCED("C1"),
    PROFICIENCY("C2");

    private String id;

    StudentLevel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static StudentLevel fromId(String id) {
        for (StudentLevel at : StudentLevel.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
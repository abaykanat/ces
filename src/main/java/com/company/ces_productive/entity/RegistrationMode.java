package com.company.ces_productive.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum RegistrationMode implements EnumClass<String> {

    PARENT("PARENT"),
    STUDENT("STUDENT");

    private final String id;

    RegistrationMode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static RegistrationMode fromId(String id) {
        for (RegistrationMode at : RegistrationMode.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
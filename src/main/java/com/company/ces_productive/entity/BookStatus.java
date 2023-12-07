package com.company.ces_productive.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum BookStatus implements EnumClass<String> {

    IN_STOCK("IN_STOCK"),
    IN_BRANCH("IN_BRANCH"),
    IN_TRANSIT("IN_TRANSIT"),
    SOLD("SOLD"),
    LOST("LOST");

    private String id;

    BookStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static BookStatus fromId(String id) {
        for (BookStatus at : BookStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
package com.company.ces_productive.entity.courses;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;

public enum CourseType implements EnumClass<String> {

    ENGLISH("ENGLISH", "event-blue", "BLUE"),
    MATHEMATICS("MATHEMATICS", "event-green", "GREEN"),
    PRESCHOOL("PRESCHOOL", "event-yellow", "YELLOW"),
    CREATION(" CREATION", "event-red", "RED"),
    OTHER("OTHER", "event-purple", "PURPLE");

    private String id;
    private String styleName;
    private final String icon;

    CourseType(String value, String styleName, String icon) {
        this.id = value;
        this.styleName = styleName;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static CourseType fromId(String id) {
        for (CourseType at : CourseType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

    public String getStyleName() {
        return styleName;
    }

    public String getIcon() {
        return icon;
    }
}

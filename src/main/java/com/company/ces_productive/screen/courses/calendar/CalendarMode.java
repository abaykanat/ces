package com.company.ces_productive.screen.courses.calendar;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum CalendarMode implements EnumClass<String> {

    DAY("DAY"),
    WEEK("WEEK"),
    MONTH("MONTH");

    private String id;

    CalendarMode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static CalendarMode fromId(String id) {
        for (CalendarMode at : CalendarMode.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
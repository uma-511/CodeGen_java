package com.warrior.gen.model;

import lombok.Getter;
import lombok.Setter;

public class QueryParam {

    @Setter @Getter
    private String name;

    @Setter @Getter
    private String remark;

    @Setter
    private String defaultValue;

    public String getDefaultValue() {
        return defaultValue == null ? "null" : defaultValue;
    }
}

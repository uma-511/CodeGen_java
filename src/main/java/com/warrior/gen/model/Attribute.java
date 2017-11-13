package com.warrior.gen.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
public class Attribute implements Serializable {

    @Setter @Getter
    private String type;

    @Setter @Getter
    private String name;

    @Setter @Getter
    private String remark;

    @Setter @Getter
    private String fieldName;

    @Setter @Getter
    private String defaultValue;

    @Setter @Getter
    private boolean isShow;

    @Setter @Getter
    private boolean isEdit;

    public Attribute(String type,String fieldName, String name, String remark) {
        this.type = type;
        this.name = name;
        this.remark = remark;
        this.fieldName = fieldName;
    }

    public Attribute(String type, String name, String fieldName, String defaultValue,String remark) {
        this.type = type;
        this.name = name;
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
        this.remark = remark;
    }
}

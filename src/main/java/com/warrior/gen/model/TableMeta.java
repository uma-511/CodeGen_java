package com.warrior.gen.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class TableMeta implements Serializable {

    @Setter
    @Getter
    private String tableName;

    @Setter @Getter
    private String primaryKey;

    @Setter
    @Getter
    private List<Attribute> attributeList = new ArrayList<>();

    @Setter
    @Getter
    private List<String> importList = new ArrayList<>();

    public void addAttribute(int type,String fieldName, String name, String remark) {
        if(remark.indexOf("\n") >= 0){
            remark = remark.substring(0,remark.indexOf("\n"));
        }
        this.attributeList.add(new Attribute(getType(type),fieldName, name, remark));
    }

    private String getType(Integer typeKey) {
        String type = "";
        switch (typeKey) {
            case Types.INTEGER:
                type = "int";
                break;
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                type = "String";
                break;
            case Types.NUMERIC:
            case Types.DOUBLE:
                type = "double";
                break;
            case Types.BIT:
                type = "boolean";
                break;
            case Types.TINYINT:
                type = "byte []";
                break;
            case Types.BIGINT:
                type = "long";
                break;
            case Types.REAL:
                type = "float";
                break;
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                type = "Date";
                if (!importList.contains("java.util.Date")) {
                    importList.add("java.util.Date");
                }
                break;
        }
        return type;
    }
}

package com.warrior.gen.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TableInfo implements Serializable {

    @Setter @Getter
    private String tableName;

    @Setter @Getter
    private String path;

    @Setter @Getter
    private String packageName;

    @Setter @Getter
    private String remark;

    @Setter @Getter
    private Boolean swagger = true;

    @Setter @Getter
    private List<QueryParam> query;

    @Setter @Getter
    private String noShow;

    @Setter @Getter
    private String noEdit;

    @Setter @Getter
    private String entityName;

    @Setter @Getter
    private String scriptPath;

    @Getter @Setter
    private boolean genView = true;

    @Getter @Setter
    private boolean genCode = true;

    @Setter @Getter
    private List<String> importList = new ArrayList<>();

    @Setter @Getter
    private List<Attribute> attrs = new ArrayList<>();

    public QueryParam getParam(String arg){
        if (query == null || query.size() == 0){
            return null;
        }
        for (QueryParam param : query){
            if (StringUtils.equals(param.getName(),arg)){
                return param;
            }
        }
        return null;
    }

    public boolean isShow(String field){
        return StringUtils.isBlank(noShow) ? true : !noShow.contains(field+",");
    }
    public boolean isEdit(String field){
        return StringUtils.isBlank(noEdit) ? true : !noEdit.contains(field+",");
    }
}

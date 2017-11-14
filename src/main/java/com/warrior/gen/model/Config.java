package com.warrior.gen.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.io.Serializable;


public class Config implements Serializable{

    @Getter @Setter
    private String driverClass;

    @Getter @Setter
    private String url;

    @Getter @Setter
    private String userName;

    @Getter @Setter
    private String passWord;

    @Getter @Setter
    private String prefix;

    @Getter @Setter
    private List<TableInfo> tableList;

    @Getter @Setter
    private String viewPath;
}

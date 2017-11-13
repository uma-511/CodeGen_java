package com.warrior.gen;

import com.google.gson.Gson;
import com.warrior.gen.database.DBHelper;
import com.warrior.gen.exception.GenException;
import com.warrior.gen.model.*;
import com.warrior.gen.util.NameUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeGen {

    private Config config;
    private DBHelper dbHelper;
    private Configuration cfg;

    public static void main(String args []) throws IOException{
        CodeGen codeGen = new CodeGen();
        String configPath = codeGen.getRunPath();
        configPath = StringUtils.isEmpty(configPath) ? System.getProperty("user.dir")+"/src/resources/config.json" : configPath+"config.json";
        String json = FileUtils.readFileToString(new File(configPath),"UTF-8");
        Config config = new Gson().fromJson(json,Config.class);

        codeGen.genCode(config);
    }

    public void genCode(Config config){
        try {
            dbHelper = DBHelper.getInstance(config);
            this.config = config;
            cfg = new Configuration(Configuration.VERSION_2_3_26);
            String rootPath = getRunPath();
            rootPath = StringUtils.isEmpty(rootPath) ? System.getProperty("user.dir")+"/src/resources/template" : rootPath+"template";

            cfg.setDirectoryForTemplateLoading(new File(rootPath));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            if (config.getTableList() != null && config.getTableList().size() > 0){
                for(TableInfo table : config.getTableList()){
                    if (StringUtils.isBlank(table.getPackageName())){
                        throw new GenException("packageName不能为空！");
                    }
                    TableMeta meta = dbHelper.getTableMeta(table.getTableName());
                    genEntity(table,meta);
                    genDao(table);
                    genMapper(table);
                    genService(table);
                    genServiceImpl(table);
                    genController(table);
                    genView(table,meta);
                    dbHelper.insert(NameUtil.getCamelCaseName(table.getEntityName(),true),table.getRemark());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e){
            e.printStackTrace();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void genView(TableInfo table,TableMeta meta)  throws IOException,TemplateException{
        String name = NameUtil.getCamelCaseName(table.getEntityName(),true);
        String filePath = getPath(config.getViewPath())+File.separator+name;

        Map<String,Object> root = new HashMap<>();
        root.put("primaryKey",NameUtil.getCamelCaseName(meta.getPrimaryKey(),true));
        root.put("name",name);
        root.put("remark",table.getRemark());
        root.put("args",table.getAttrs());
        for (Attribute attr : meta.getAttributeList()){
            attr.setShow(table.isShow(attr.getFieldName()));
            attr.setEdit(table.isEdit(attr.getFieldName()));
        }
        root.put("attrs",meta.getAttributeList());
        File dir = new File(filePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        genFile("view.ftl",new File(dir,"index.vue"),root);
    }

    private void genController(TableInfo table) throws IOException,TemplateException{
        String packagePath = (table.getPackageName() + ".controller").replace(".","/");
        String classPath = getPath(table.getPath()) + File.separator +"src/main/java/"+packagePath;
        String className = table.getEntityName()+"Controller";
        Map<String,Object> root = new HashMap<>();
        table.getImportList().clear();
        for(Attribute attr : table.getAttrs()){
            if(StringUtils.equals("Date",attr.getType())){
                table.getImportList().add("java.util.Date");
            }
        }
        root.put("packageName",table.getPackageName());
        root.put("entityName",table.getEntityName());
        root.put("className",className);
        root.put("name",NameUtil.getCamelCaseName(table.getEntityName(),true));
        root.put("swagger",table.getSwagger());
        root.put("remark",table.getRemark());
        root.put("args",table.getAttrs());
        root.put("imports",table.getImportList());
        File dir = new File(classPath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        genFile("controller.ftl",new File(dir,className+".java"),root);
    }

    private void genServiceImpl(TableInfo table) throws IOException,TemplateException{
        String packagePath = (table.getPackageName() + ".service.impl").replace(".","/");
        String classPath = getPath(table.getPath()) + File.separator +"src/main/java/"+packagePath;
        String className = table.getEntityName()+"ServiceImpl";
        Map<String,Object> root = new HashMap<>();
        table.getImportList().clear();
        for(Attribute attr : table.getAttrs()){
            if(StringUtils.equals("Date",attr.getType())){
                table.getImportList().add("java.util.Date");
            }else if(StringUtils.equals("String",attr.getType())){
                table.getImportList().add("org.apache.commons.lang.StringUtils");
            }
        }
        root.put("packageName",table.getPackageName());
        root.put("entityName",table.getEntityName());
        root.put("className",className);
        root.put("args",table.getAttrs());
        root.put("imports",table.getImportList());

        File dir = new File(classPath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        genFile("serviceImpl.ftl",new File(dir,className+".java"),root);
    }

    private void genService(TableInfo table) throws IOException,TemplateException{
        String packagePath = (table.getPackageName() + ".service").replace(".","/");
        String classPath = getPath(table.getPath()) + File.separator +"src/main/java/"+packagePath;
        String className = table.getEntityName()+"Service";
        Map<String,Object> root = new HashMap<>();
        table.getImportList().clear();
        for(Attribute attr : table.getAttrs()){
            if(StringUtils.equals("Date",attr.getType())){
                table.getImportList().add("java.util.Date");
            }
        }

        root.put("packageName",table.getPackageName());
        root.put("entityName",table.getEntityName());
        root.put("className",className);
        root.put("args",table.getAttrs());
        root.put("imports",table.getImportList());

        File dir = new File(classPath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        genFile("service.ftl",new File(dir,className+".java"),root);
    }
    private void genMapper(TableInfo tableInfo) throws IOException,TemplateException {
        String packagePath = (tableInfo.getPackageName() + ".mapper").replace(".","/");
        String classPath = getPath(tableInfo.getPath()) + File.separator +"src/main/java/"+packagePath;
        String className = tableInfo.getEntityName()+".mapper.xml";
        Map<String,Object> root = new HashMap<>();
        root.put("packageName",tableInfo.getPackageName());
        root.put("entityName",tableInfo.getEntityName());
        root.put("tableName",tableInfo.getTableName());
        File dir = new File(classPath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        genFile("mapper.ftl",new File(dir,className),root);
    }

    private void genDao(TableInfo tableInfo) throws IOException,TemplateException{
        String packagePath = (tableInfo.getPackageName() + ".dao").replace(".","/");
        String classPath = getPath(tableInfo.getPath()) + File.separator +"src/main/java/"+packagePath;
        String className = tableInfo.getEntityName()+"Dao";
        Map<String,Object> root = new HashMap<>();
        root.put("packageName",tableInfo.getPackageName());
        root.put("entityName",tableInfo.getEntityName());
        root.put("className",className);

        File dir = new File(classPath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        genFile("dao.ftl",new File(dir,className+".java"),root);
    }

    private void genEntity(TableInfo tableInfo, TableMeta meta) throws IOException,TemplateException{
        boolean hasPerfix = StringUtils.isBlank(config.getPrefix()) ? false : true;
        String packagePath = (tableInfo.getPackageName() + ".entity").replace(".","/");
        String classPath = getPath(tableInfo.getPath()) + File.separator +"src/main/java/"+packagePath;
        String className = NameUtil.getCamelCaseName(
                hasPerfix ? tableInfo.getTableName().replace(config.getPrefix(),"") : tableInfo.getTableName()
                ,false);
        tableInfo.setEntityName(className);
        Map<String,Object> root = new HashMap<>();
        root.put("packageName",tableInfo.getPackageName());
        root.put("className",className);
        root.put("tableName",tableInfo.getTableName());
        root.put("remark",tableInfo.getRemark());
        root.put("swagger",tableInfo.getSwagger());
        root.put("primaryKey",meta.getPrimaryKey());
        if (tableInfo.getSwagger()){
            meta.getImportList().add("io.swagger.annotations.ApiModel");
            meta.getImportList().add("io.swagger.annotations.ApiModelProperty");
        }
        root.put("imports",meta.getImportList());

        List<Attribute> attrs = meta.getAttributeList();
        String temp = "";
        QueryParam param = null;
        for (Attribute attr : attrs){
            temp = hasPerfix ?
                    NameUtil.getCamelCaseName(attr.getName().replace(config.getPrefix(),""),true) :
                    NameUtil.getCamelCaseName(attr.getName(),true);
            param = tableInfo.getParam(attr.getName());
            if(param != null && StringUtils.equals(attr.getName(),param.getName())){
                tableInfo.getAttrs().add(new Attribute(attr.getType(),temp,attr.getName(),param.getDefaultValue(),param.getRemark()));
            }
            attr.setName(temp);
        }
        root.put("attrs",attrs);
        File dir = new File(classPath);
        if (!dir.exists()){
            dir.mkdirs();
        }

        genFile("entity.ftl",new File(dir,className+".java"),root);
    }

    private void genFile(String templateName,File file,Map<String,Object> root) throws IOException,TemplateException{
        Template tpl = cfg.getTemplate(templateName);
        OutputStream fos = new FileOutputStream(file);
        Writer out = new OutputStreamWriter(fos);
        tpl.process(root,out);
        out.flush();
        out.close();
        fos.flush();
        fos.close();
    }
    private String getPath(String path){
        return StringUtils.isEmpty(path) ? System.getProperty("user.dir") : path;
    }

    private String getRunPath() throws IOException{
        String path = null;
        URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
        if (StringUtils.endsWith(url.getPath(),".jar")){
            path = URLDecoder.decode(url.getPath(),"UTF-8");
            path = path.substring(0,path.lastIndexOf("/")+1);
        }
        return path;
    }
}

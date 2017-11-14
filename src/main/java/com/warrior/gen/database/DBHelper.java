package com.warrior.gen.database;

import com.warrior.gen.model.Config;
import com.warrior.gen.model.TableMeta;
import org.codehaus.groovy.runtime.GStringImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper {

    public static DBHelper dbHelper;

    private Connection connection;
    private DatabaseMetaData metaData;

    private DBHelper(Config config) {
        try {
            Class.forName(config.getDriverClass());
            connection = DriverManager.getConnection(config.getUrl(), config.getUserName(), config.getPassWord());
            metaData = connection.getMetaData();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static DBHelper getInstance(Config config) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(config);
        }
        return dbHelper;
    }

    public TableMeta getTableMeta(String tableName) throws SQLException {
        TableMeta tableMeta = new TableMeta();
        ResultSet rst = metaData.getTables(null, "%", tableName, new String[]{"TABLE"});
        if (rst.next()){
            tableMeta.setTableName(rst.getString("TABLE_NAME"));
        }
        rst = metaData.getPrimaryKeys(null,null,tableName);
        String primaryKey = "";
        if (rst.next()){
            primaryKey = rst.getString("COLUMN_NAME");
        }
        rst = metaData.getColumns(null, "%", tableName, "%");
        tableMeta.setPrimaryKey(primaryKey);
        while(rst.next()){
            tableMeta.addAttribute(rst.getInt("DATA_TYPE"),rst.getString("COLUMN_NAME"),rst.getString("COLUMN_NAME"),rst.getString("REMARKS"));
        }
        return tableMeta;
    }


    public void setAutoCommit(boolean autoCommit){
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void rollback(){
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void commit(){
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int insert(String sql,Object ... params){
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int id =0;
        try {
            pstmt = connection.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
            if (params != null && params.length > 0){
                for (int i=0,j=params.length;i<j;i++){
                    pstmt.setObject(i+1,params[i] instanceof GStringImpl ? params[i].toString() : params[i]);
                }
            }
            if (pstmt.executeUpdate() > 0){
                rs = pstmt.getGeneratedKeys();
                rs.next();
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    public boolean executeSql(String sql,Object ... params){
        PreparedStatement pstmt = null;
        int ret = -1;
        try {
            pstmt = connection.prepareStatement(sql);
            if (params != null && params.length > 0){
                for (int i=0,j=params.length;i<j;i++){
                    pstmt.setObject(i+1,params[i] instanceof GStringImpl ? params[i].toString() : params[i]);
                }
            }
            ret = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ret > 0;
    }

    public List<Map<String,Object>> query(String sql,Object ... params){
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String,Object>> dataList = new ArrayList<>();
        try {
            pstmt = connection.prepareStatement(sql);
            if (params != null && params.length > 0){
                for (int i=0,j=params.length;i<j;i++){
                    pstmt.setObject(i+1,params[i] instanceof GStringImpl ? params[i].toString() : params[i]);
                }
            }
            rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            Map<String,Object> data = null;
            int count = metaData.getColumnCount();
            while (rs.next()){
                data = new HashMap<>();
                for (int i =0;i<count;i++){
                    data.put(metaData.getColumnName(i+1),rs.getObject(i+1));
                }
                dataList.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    public Map<String,Object> querySingle(String sql,Object ... params){
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String,Object> data = null;
        try {
            pstmt = connection.prepareStatement(sql);
            if (params != null && params.length > 0){
                for (int i=0,j=params.length;i<j;i++){
                    pstmt.setObject(i+1,params[i] instanceof GStringImpl ? params[i].toString() : params[i]);
                }
            }
            rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            if(rs.next()){
                data = new HashMap<>();
                for (int i =0;i<count;i++){
                    data.put(metaData.getColumnName(i+1),rs.getObject(i+1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
}

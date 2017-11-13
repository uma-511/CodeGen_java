package com.warrior.gen.database;

import com.warrior.gen.model.Config;
import com.warrior.gen.model.TableMeta;

import java.sql.*;

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

    public void insert(String name,String remark) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection.setAutoCommit(false);
            String sql = "SELECT res_id FROM warrior_resources WHERE res_name = ? AND permission = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setObject(1,remark);
            pstmt.setObject(2,"admin:"+name+":view");
            rs = pstmt.executeQuery();
            if(rs.next()){
                pstmt = connection.prepareStatement("DELETE FROM warrior_resources WHERE res_name = ? AND permission = ?");
                pstmt.setObject(1,remark);
                pstmt.setObject(2,"admin:"+name+":view");
                pstmt.executeUpdate();
                pstmt = connection.prepareStatement("DELETE FROM warrior_resources WHERE parent_id = ?");
                pstmt.setObject(1,rs.getInt(1));
                pstmt.executeUpdate();
            }

            sql = "INSERT INTO warrior_resources (res_name,parent_id,url,sort,is_show,remark,status,create_time,update_time,permission,icon,type) VALUES (?,0,?,1,0,'',0,now(),now(),?,'briefcase',0)";
            pstmt = connection.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setObject(1,remark);
            pstmt.setObject(2,"/"+name+"/list");
            pstmt.setObject(3,"admin:"+name+":view");
            if(pstmt.executeUpdate() > 0){
                rs = pstmt.getGeneratedKeys();
                rs.next();
                int id = rs.getInt(1);
                if (id > 0){
                    sql = "INSERT INTO warrior_resources (res_name,parent_id,url,sort,is_show,remark,status,create_time,update_time,permission,icon,type) " +
                            "SELECT ?,?,'',0,0,'',0,now(),now(),?,'',1 UNION " +
                            "SELECT ?,?,'',0,0,'',0,now(),now(),?,'',1 UNION " +
                            "SELECT ?,?,'',0,0,'',0,now(),now(),?,'',1 ";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setObject(1,"添加");
                    pstmt.setObject(2,id);
                    pstmt.setObject(3,"admin:"+name+":add");
                    pstmt.setObject(4,"修改");
                    pstmt.setObject(5,id);
                    pstmt.setObject(6,"admin:"+name+":update");
                    pstmt.setObject(7,"删除");
                    pstmt.setObject(8,id);
                    pstmt.setObject(9,"admin:"+name+":del");
                    if(pstmt.executeUpdate() > 0){
                        connection.commit();
                    }else{
                        connection.rollback();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

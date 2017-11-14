package script

import com.warrior.gen.util.FileUtil

def name = FileUtil.getCamelCaseName(tableInfo.getEntityName(),true);

def data = dbHelper.querySingle("SELECT res_id FROM warrior_resources WHERE res_name = ? AND permission = ?",tableInfo.getRemark(),"admin:$name:view");
dbHelper.setAutoCommit(false)
if(data != null){
    dbHelper.executeSql("DELETE FROM warrior_resources WHERE res_name = ? AND permission = ?",tableInfo.getRemark(),"admin:$name:view");
    dbHelper.executeSql("DELETE FROM warrior_resources WHERE parent_id = ?",data.res_id);
}

def id = dbHelper.insert("""INSERT INTO warrior_resources (res_name,parent_id,url,sort,is_show,remark,status,create_time,update_time,permission,icon,type)
                            VALUES (?,0,?,1,0,'',0,now(),now(),?,'briefcase',0)""",tableInfo.getRemark(),"/$name/list","admin:$name:view")
if(id > 0){
    dbHelper.executeSql("""INSERT INTO warrior_resources (res_name,parent_id,url,sort,is_show,remark,status,create_time,update_time,permission,icon,type)
                           SELECT ?,?,'',0,0,'',0,now(),now(),?,'',1 UNION
                           SELECT ?,?,'',0,0,'',0,now(),now(),?,'',1 UNION
                           SELECT ?,?,'',0,0,'',0,now(),now(),?,'',1""",
                           "添加",id,"admin:$name:add","修改",id,"admin:$name:update","删除",id,"admin:$name:del")
    dbHelper.commit()
}else{
    dbHelper.rollback()
}

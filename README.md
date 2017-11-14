##代码生成配置说明
```
{
  "driverClass":"com.mysql.jdbc.Driver",
  "url":"jdbc:mysql://loaction:3306/warrior?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull",
  "userName":"root",
  "passWord":"123456",
  "prefix":"warrior_",
  "viewPath":"/Users/rookie/Workspace/CodeRepository/ManageSystem/com.warrior.view/views/src/views",
  "tableList":[
    {
      "tableName":"warrior_test",
      "path":"/Users/rookie/Workspace/CodeRepository/ManageSystem/com.warrior.test",
      "packageName":"com.warrior.test",
      "remark":"测试信息",
      "query":[
        {
          "name":"name",
          "remark":"用户名",
          "defaultValue":""
        },
        {
          "name":"create_time",
          "remark":"创建时间",
          "defaultValue":""
        }
      ],
      "noShow":"id,",
      "noEdit":"id,create_time,",
      "swagger":true,
      "genView":false,
      "scriptPath":"warrior.groovy"
    }
  ]
}
```
说明：
- driverClass：数据库驱动
- url：数据库连接
- userName：数据库用户名
- passWord：数据库密码
- prefix：数据库表名前缀
- viewPath：生成页面存放路径
- tableList.tableName：表名
- tableList.path：生成类的存放路径
- tableList.packageName：包名
- tableList.remark：表说明信息[用于菜单生成和api说明]
- tableList.noShow：页面表格不需要显示的字段名
- tableList.noEdit：不需要修改的字段名
- tableList.swagger：是否生成API文档
- tableList.genView：是否生成前端页面
- tableList.scriptPath：脚本路径 用于执行额外的操作
- tableList.query.name：分页查询条件字段名
- tableList.query.remark：分页查询条件字段说明
- tableList.query.defaultValue：分页查询条件默认值

##代码生成功能使用
### 一、源代码运行
1. 修改 resources/config.json 配置文件
2. 运行 com.warrior.gen.App 即可

## 二、打成jar包运行
1. 切换到项目目录 运行 mvn clean package
2. 拷贝 target/CodeGen-1.0.zip 文件到其他目录
3. 解压 CodeGen-1.0.zip 文件
4. 修改 目录下的 config.json 文件
5. 运行 java -jar CodeGen-1.0.jar 

- template目录存放模板文件 lib目录存放引用的第三方jar文件 script目录存放groovy脚本文件

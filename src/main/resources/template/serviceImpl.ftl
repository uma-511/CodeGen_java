package ${packageName}.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.warrior.common.service.impl.WarriorBaseServiceImpl;
import org.springframework.stereotype.Service;
import ${packageName}.service.${entityName}Service;
import ${packageName}.dao.${entityName}Dao;
import ${packageName}.entity.${entityName};
<#list imports as imp>
import ${imp};
</#list>

@Service
public class ${className} extends WarriorBaseServiceImpl<${entityName}Dao, ${entityName}> implements ${entityName}Service {

    public Page<${entityName}> getPageList(Page<${entityName}> page <#list args as arg><#if arg.type == "Date">,${arg.type} ${arg.name}_start,${arg.type} ${arg.name}_end<#else>,${arg.type} ${arg.name}</#if></#list>) {
        EntityWrapper<${entityName}> ew = new EntityWrapper<>();
        <#list args as arg>
        <#if arg.type == "Date">
        if(${arg.name}_start != null){
            ew.ge("${arg.fieldName}",${arg.name}_start);
        }
        if(${arg.name}_end != null){
            ew.le("${arg.fieldName}",${arg.name}_end);
        }
        <#elseif arg.type == "String">
        if(!StringUtils.isBlank(${arg.name})){
            ew.eq("${arg.fieldName}",${arg.name});
        }
        <#else>
        if(${arg.name} != ${arg.defaultValue}){
            ew.eq("${arg.fieldName}",${arg.name});
        }
        </#if>
        </#list>
        page.setRecords(baseMapper.getPageList(page, ew));
        return page;
   }

}
package ${packageName}.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import ${packageName}.entity.${entityName};
<#list imports as imp>
import ${imp};
</#list>

public interface ${className} extends IService<${entityName}>{

    Page<${entityName}> getPageList(Page<${entityName}> page<#list args as arg><#if arg.type == "Date">,${arg.type} ${arg.name}_start,${arg.type} ${arg.name}_end<#else>,${arg.type} ${arg.name}</#if></#list>);

}

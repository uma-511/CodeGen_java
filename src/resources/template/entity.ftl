package ${packageName}.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
<#list imports as imp>
import ${imp};
</#list>

<#if swagger>
@ApiModel(value = "${className}",description = "${remark}")
</#if>
@TableName(value = "${tableName}")
public class ${className} implements Serializable{

<#list attrs as attr>
   <#if swagger>
   @ApiModelProperty(value = "${attr.remark}")
   </#if>
   <#if attr.fieldName == primaryKey>
   @TableId
   </#if>
   @Setter @Getter
   private ${attr.type} ${attr.name};

</#list>
}
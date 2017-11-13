package ${packageName}.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import ${packageName}.entity.${entityName};

public interface ${className} extends BaseMapper<${entityName}> {

    List<${entityName}> getPageList(Pagination page, @Param("ew")Wrapper<${entityName}> wrapper);

}
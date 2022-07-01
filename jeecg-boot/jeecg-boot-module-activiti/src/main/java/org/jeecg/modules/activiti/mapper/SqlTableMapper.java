package org.jeecg.modules.activiti.mapper;

import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SqlTableMapper {
    @Select("select name from SYSOBJECTS where type='u'")
//    SELECT NAME FROM SYSOBJECTS WHERE TYPE='U'
    List<String> getTable();

}

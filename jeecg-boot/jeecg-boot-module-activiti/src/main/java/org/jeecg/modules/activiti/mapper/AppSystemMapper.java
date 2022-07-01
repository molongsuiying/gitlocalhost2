package org.jeecg.modules.activiti.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.activiti.entity.AppSystem;

import java.util.List;

public interface AppSystemMapper extends BaseMapper<AppSystem> {
    @Select("select name,url from app_system")
    List<AppSystem> selectNameAndUrl();
    @Select("select name,url from app_system where id!=#{id}")
    List<AppSystem> selectUrl(@Param("id") String id);
}

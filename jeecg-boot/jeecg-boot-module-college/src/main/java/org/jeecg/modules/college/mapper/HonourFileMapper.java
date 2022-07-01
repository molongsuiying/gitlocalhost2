package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.college.entity.HonourFile;

import java.util.List;
import java.util.Map;

public interface HonourFileMapper extends BaseMapper<HonourFile>{

    List<HonourFile> queryList(Map<String, Object> params);

    int countList(Map<String, Object> params);

    void passBatch(@Param("ids")List<String> ids);

    void submitBatch(@Param("ids")List<String> ids);

    List<HonourFile> queryListByIds(@Param("ids")List<String> ids);

    HonourFile findById(@Param("id")String id);

    HonourFile queryList(String id);

    @Select("select * from ${tableName} where id = #{tableId}")
    Map<String,Object> getBusiData(@Param("tableId") String tableId, @Param("tableName") String tableName);
}

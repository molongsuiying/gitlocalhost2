package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.HonourProject;

import java.util.List;
import java.util.Map;

public interface HonourProjectMapper extends BaseMapper<HonourProject>{

    List<HonourProject> queryList(Map<String, Object> params);

    int countList(Map<String, Object> params);

    void passBatch(@Param("ids") List<String> ids);

    void submitBatch(@Param("ids") List<String> ids);

    List<HonourProject> queryListByIds(@Param("ids") List<String> ids);

    HonourProject findById(@Param("id")String id);
}

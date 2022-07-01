package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.HonourPerson;

import java.util.List;
import java.util.Map;

public interface HonourPersonMapper extends BaseMapper<HonourPerson>{

    List<HonourPerson> queryList(Map<String, Object> params);

    int countList(Map<String, Object> params);

    void passBatch(@Param("ids")List<String> ids);

    void submitBatch(@Param("ids")List<String> ids);

    List<HonourPerson> queryListByIds(@Param("ids")List<String> ids);

    HonourPerson findById(@Param("id")String id);
}

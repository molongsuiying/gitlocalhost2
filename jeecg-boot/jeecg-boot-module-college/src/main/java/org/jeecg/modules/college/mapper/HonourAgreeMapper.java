package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.HonourAgree;

import java.util.List;
import java.util.Map;

public interface HonourAgreeMapper extends BaseMapper<HonourAgree>{

    List<HonourAgree> queryList(Map<String, Object> params);

    int countList(Map<String, Object> params);

    void passBatch(List<String> ids);

    void submitBatch(@Param("ids")List<String> ids);

    List<HonourAgree> queryListByIds(@Param("ids")List<String> ids);

    HonourAgree findById(@Param("id")String id);
}

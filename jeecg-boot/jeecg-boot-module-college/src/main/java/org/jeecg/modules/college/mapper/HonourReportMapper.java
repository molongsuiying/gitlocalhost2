package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.HonourReport;

import java.util.List;
import java.util.Map;

public interface HonourReportMapper extends BaseMapper<HonourReport>{

    List<HonourReport> queryList(Map<String, Object> params);

    int countList(Map<String, Object> params);

    void passBatch(@Param("ids")List<String> ids);

    void submitBatch(@Param("ids")List<String> ids);

    List<HonourReport> queryListByIds(@Param("ids")List<String> ids);

    HonourReport findById(@Param("id")String id);
}

package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.college.entity.HonourAppendixHistory;
import org.jeecg.modules.college.vo.HonourDataVo;

import java.util.List;
import java.util.Map;

public interface HonourAppendixHistoryMapper extends BaseMapper<HonourAppendixHistory>{


    List<HonourDataVo> findQueryDataList(Map<String,Object> params);
}

package org.jeecg.modules.college.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.HonourAppendix;
import org.jeecg.modules.college.entity.HonourQueryHistory;
import org.jeecg.modules.college.vo.HonourDataVo;

import java.util.List;
import java.util.Map;

public interface HonourQueryHistoryService extends IService<HonourQueryHistory>{

    void removeAll();

    JSONObject countByTime(Map<String,Object> params);
}

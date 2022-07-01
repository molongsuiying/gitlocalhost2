package org.jeecg.modules.college.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.HonourAppendix;
import org.jeecg.modules.college.entity.HonourAppendixHistory;
import org.jeecg.modules.college.vo.AppendixVo;

import java.util.List;
import java.util.Map;

public interface HonourAppendixHistoryService extends IService<HonourAppendixHistory>{

    void saveHistories(List<AppendixVo> vos);

    JSONObject countByTime(Map<String,Object> params);
}

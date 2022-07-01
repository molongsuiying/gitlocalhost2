package org.jeecg.modules.college.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.college.entity.HonourAppendix;
import org.jeecg.modules.college.entity.HonourQueryHistory;
import org.jeecg.modules.college.mapper.HonourAppendixMapper;
import org.jeecg.modules.college.mapper.HonourQueryHistoryMapper;
import org.jeecg.modules.college.service.HonourAppendixService;
import org.jeecg.modules.college.service.HonourQueryHistoryService;
import org.jeecg.modules.college.vo.HonourDataVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@Service
public class HonourQueryHistoryServiceImpl extends ServiceImpl<HonourQueryHistoryMapper, HonourQueryHistory> implements HonourQueryHistoryService{


    @Resource
    private HonourQueryHistoryMapper historyMapper;
    @Override
    public void removeAll() {
        historyMapper.removeAll();
    }

    @Override
    public JSONObject countByTime(Map<String, Object> params) {

        List<HonourDataVo> dataVos = historyMapper.findQueryDataList(params);
        SimpleDateFormat sdf=new SimpleDateFormat("dd日");
        JSONObject object = new JSONObject();
        for (int i = 0; i < dataVos.size(); i++) {
            String createTime = sdf.format(dataVos.get(i).getCreateTime());
            dataVos.get(i).setTime(createTime);
        }
        Map<String,List<HonourDataVo>> result = dataVos.parallelStream().collect(Collectors.groupingBy(HonourDataVo::getTime));
        for (Map.Entry<String, List<HonourDataVo>> entry : result.entrySet()) {
            object.put(entry.getKey(),entry.getValue().size());
        }
        return object;
    }
}

package org.jeecg.modules.college.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.college.entity.HonourAppendix;
import org.jeecg.modules.college.entity.HonourAppendixHistory;
import org.jeecg.modules.college.mapper.HonourAppendixHistoryMapper;
import org.jeecg.modules.college.mapper.HonourAppendixMapper;
import org.jeecg.modules.college.service.HonourAppendixHistoryService;
import org.jeecg.modules.college.service.HonourAppendixService;
import org.jeecg.modules.college.vo.AppendixVo;
import org.jeecg.modules.college.vo.HonourDataVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@Service
public class HonourAppendixHistoryServiceImpl extends ServiceImpl<HonourAppendixHistoryMapper, HonourAppendixHistory> implements HonourAppendixHistoryService{


    @Resource
    private HonourAppendixHistoryMapper historyMapper;

    @Override
    public void saveHistories(List<AppendixVo> vos) {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Date now = new Date();
        List<HonourAppendixHistory> histories = new ArrayList<>();
        for (int i = 0; i < vos.size(); i++) {
            AppendixVo vo = vos.get(i);
            HonourAppendixHistory history = new HonourAppendixHistory();
            history.setAppendixIds(vo.getAppendixIds());
            history.setUserId(sysUser.getId());
            history.setDownloadTime(now);
            history.setHonourTitle(vo.getTitle());
            history.setHonourType(vo.getHonourClass());
            histories.add(history);

        }
        this.saveBatch(histories);
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

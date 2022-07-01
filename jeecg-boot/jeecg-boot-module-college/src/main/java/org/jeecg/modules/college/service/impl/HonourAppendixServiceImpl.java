package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.college.entity.HonourAppendix;
import org.jeecg.modules.college.entity.HonourAppendixHistory;
import org.jeecg.modules.college.mapper.HonourAppendixHistoryMapper;
import org.jeecg.modules.college.mapper.HonourAppendixMapper;
import org.jeecg.modules.college.service.HonourAppendixService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@Service
public class HonourAppendixServiceImpl extends ServiceImpl<HonourAppendixMapper, HonourAppendix> implements HonourAppendixService{


    @Resource
    private HonourAppendixMapper appendixMapper;


    @Override
    public List<HonourAppendix> queryListById(List<String> ids) {
        return appendixMapper.queryListById(ids);
    }

}

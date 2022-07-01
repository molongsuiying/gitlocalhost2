package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.college.entity.HonourAuthItem;
import org.jeecg.modules.college.mapper.HonourAuthItemMapper;
import org.jeecg.modules.college.service.HonourAuthItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@Service
public class HonourAuthItemServiceImpl extends ServiceImpl<HonourAuthItemMapper, HonourAuthItem> implements HonourAuthItemService{


    @Autowired
    private HonourAuthItemMapper itemMapper;
    @Override
    public HonourAuthItem findItemByUser(Integer honourClass) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        return itemMapper.findItemByUserAndType(sysUser.getId(),honourClass);
    }
}

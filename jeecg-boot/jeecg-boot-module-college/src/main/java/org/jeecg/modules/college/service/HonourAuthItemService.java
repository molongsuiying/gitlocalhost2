package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.HonourAuth;
import org.jeecg.modules.college.entity.HonourAuthItem;
import org.jeecg.modules.college.vo.AuthVo;

import java.util.List;
import java.util.Map;

public interface HonourAuthItemService extends IService<HonourAuthItem>{

    HonourAuthItem findItemByUser(Integer honourClass);
}

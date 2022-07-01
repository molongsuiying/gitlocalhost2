package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.HonourAuth;
import org.jeecg.modules.college.entity.HonourAuthItem;
import org.jeecg.modules.college.vo.AuthVo;

import java.util.List;
import java.util.Map;

public interface HonourAuthService extends IService<HonourAuth>{

    List<HonourAuth> queryList(Map<String, Object> params);

    long countList(Map<String, Object> params);

    List<HonourAuthItem> queryItemList(Map<String, Object> params);

    long countItemList(Map<String, Object> params);

    int findByAuthIdAndType(String userId,int honourType);

    void removeByUserId(String userId);

    void batchSave(AuthVo vo);

    void updateAuthEvery();
}

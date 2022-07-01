package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.HonourAuth;
import org.jeecg.modules.college.entity.HonourAuthItem;

import java.util.List;
import java.util.Map;

public interface HonourAuthItemMapper extends BaseMapper<HonourAuthItem> {

    List<HonourAuthItem> queryList(Map<String, Object> params);

    int countList(Map<String, Object> params);

    int findByAuthIdAndType(@Param("authId")String authId,@Param("honourType")int honourType);

    void removeByUserId(@Param("userId")String userId);

    HonourAuthItem findItemByUserAndType(@Param("userId")String userId,@Param("honourType")int honourType);

    void updateExportAuthEveryDay();

    void updateDownAuthEveryDay();
}

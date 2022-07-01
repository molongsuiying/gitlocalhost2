package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.HonourAuth;

import java.util.List;
import java.util.Map;

public interface HonourAuthMapper extends BaseMapper<HonourAuth> {

    List<HonourAuth> queryList(Map<String, Object> params);

    int countList(Map<String, Object> params);

    List<HonourAuth> queryListByUser(Map<String, Object> params);

    int countListByUser(Map<String, Object> params);

    int findByUserIdAndType(@Param("userId")String userId,@Param("honourType")int honourType);

    void removeByUserId(@Param("userId")String userId);

    void updateExportAuthEveryDay();

    void updateDownAuthEveryDay();

}

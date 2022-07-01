package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.AppAchievement;

import java.util.List;
import java.util.Map;
@Mapper
public interface AppAchievementMapper extends BaseMapper<AppAchievement>{

    List<AppAchievement> queryList(Map<String, Object> params);

    int countList(Map<String, Object> params);

    void passBatch(@Param("ids")List<String> ids);

    void submitBatch(@Param("ids")List<String> ids);

    List<AppAchievement> queryListByIds(@Param("ids")List<String> ids);

    AppAchievement findById(@Param("id")String id);

}

package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.AppAccident;

import java.util.List;
import java.util.Map;

public interface AppAccidentMapper extends BaseMapper<AppAccident>{

    List<AppAccident> queryList(Map<String, Object> params);

    int countList(Map<String, Object> params);

    void passBatch(@Param("ids")List<String> ids);

    void submitBatch(@Param("ids")List<String> ids);

    List<AppAccident> queryListByIds(@Param("ids")List<String> ids);

    AppAccident findById(@Param("id")String id);

}

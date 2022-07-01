package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.Honour;
import org.jeecg.modules.college.vo.CommonVo;
import org.jeecg.modules.college.vo.HonourDataVo;
import org.jeecg.modules.college.vo.HonourEntityVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface HonourMapper extends BaseMapper<Honour>{

    List<Honour> queryList(Map<String,Object> params);

    int countList(Map<String,Object> params);

    void passBatch(List<String> ids);

    void passByStudentId(String studentId);

    List<CommonVo> queryDictMap(@Param("dict")String dict);

    String getUserIdByUsername(@Param("username")String username);

    List<HonourEntityVo> findHonourAllList(Map<String,Object> params);

    long countHonourAllList(Map<String,Object> params);

    List<HonourDataVo> findDataVoList(Map<String,Object> params);

    List<String> findValuesByHonour(Map<String,Object> params);

    List<HonourEntityVo> findDuplicateItemByTitle(Map<String,Object> params);
}

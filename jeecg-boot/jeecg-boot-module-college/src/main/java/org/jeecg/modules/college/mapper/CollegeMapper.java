package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.college.entity.College;
import org.jeecg.modules.college.vo.CommonVo;

import java.util.List;
import java.util.Map;

public interface CollegeMapper extends BaseMapper<College>{

    List<CommonVo> findMyCollegeNames(Map<String,Object> queryParams);

    List<CommonVo> findNamesByRegister();

    List<College> findMyColleges(Map<String,Object> queryParams);

    String findNameById(String id);
}

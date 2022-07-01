package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.Department;
import org.jeecg.modules.college.vo.DepartVo;

import java.util.List;
import java.util.Map;

public interface DepartmentMapper extends BaseMapper<Department>{

    long countList(Map<String,Object> queryParam);

    List<DepartVo> queryList(Map<String,Object> queryParam);

    int countDepartmentByCollegeId(@Param("id")String id);

    List<DepartVo> findListByCollegeId(Map<String,Object> queryParam);
}

package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.StudentCollege;

public interface StudentCollegeMapper extends BaseMapper<StudentCollege>{

    StudentCollege findOne(@Param("bunchId")String bunchId);
}

package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.Student;
import org.jeecg.modules.college.vo.StudentVo;

import java.util.List;
import java.util.Map;

public interface StudentMapper extends BaseMapper<Student>{

    int duplicateCheck(@Param("id")String id,@Param("bunchId")String bunchId,@Param("studentCode")String studentCode);

    long countList(Map<String,Object> queryParam);

    List<StudentVo> queryList(Map<String,Object> queryParam);
}

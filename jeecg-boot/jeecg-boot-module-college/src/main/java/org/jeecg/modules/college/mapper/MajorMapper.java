package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.Major;
import org.jeecg.modules.college.vo.MajorVo;

import java.util.List;
import java.util.Map;

public interface MajorMapper extends BaseMapper<Major>{


    long countList(Map<String,Object> queryParam);

    List<MajorVo> queryList(Map<String,Object> queryParam);

    int countMajorByDepartId(@Param("id")String id);

    List<String> getMajorNamesByDepartId(@Param("id")String id);

    List<MajorVo> findListByDepartmentId(Map<String,Object> queryParam);

    String findNameById(String id);

    String findIdByName(@Param("name") String name);
}

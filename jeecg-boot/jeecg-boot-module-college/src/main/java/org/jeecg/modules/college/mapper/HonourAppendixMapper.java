package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.HonourAgree;
import org.jeecg.modules.college.entity.HonourAppendix;

import java.util.List;
import java.util.Map;

public interface HonourAppendixMapper extends BaseMapper<HonourAppendix>{

    List<HonourAppendix> queryListById(@Param("ids") List<String> ids);

}

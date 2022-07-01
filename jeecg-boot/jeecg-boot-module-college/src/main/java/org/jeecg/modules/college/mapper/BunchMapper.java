package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.college.entity.Bunch;
import org.jeecg.modules.college.vo.BunchVo;

import java.util.List;
import java.util.Map;

public interface BunchMapper extends BaseMapper<Bunch>{

    List<BunchVo> findListByMajorId(Map<String,Object> queryParam);
}

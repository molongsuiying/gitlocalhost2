package org.jeecg.modules.activiti.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.activiti.entity.ActZprocess;

import java.util.List;
import java.util.Map;

/**
 * @Description: 流程定义扩展表
 * @Author: pmc
 * @Date:   2020-03-22
 * @Version: V1.0
 */
public interface ActZprocessMapper extends BaseMapper<ActZprocess> {


    List<ActZprocess> selectZprocessList(Map<String,Object> map);

    int countZprocess(Map<String,Object> map);
}

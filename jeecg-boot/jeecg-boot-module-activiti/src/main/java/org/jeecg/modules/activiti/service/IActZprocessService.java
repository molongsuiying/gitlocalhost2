package org.jeecg.modules.activiti.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.activiti.entity.ActZprocess;

import java.util.List;
import java.util.Map;

/**
 * @Description: 流程定义扩展表
 * @Author: pmc
 * @Date:   2020-03-22
 * @Version: V1.0
 */
public interface IActZprocessService extends IService<ActZprocess> {

    List<ActZprocess> selectZprocessList(Map<String,Object> map);

    int countZprocess(Map<String,Object> map);


}

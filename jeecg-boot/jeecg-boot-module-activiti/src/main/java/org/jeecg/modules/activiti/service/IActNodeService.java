package org.jeecg.modules.activiti.service;

import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.activiti.entity.ActNode;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.activiti.entity.ActNodeUnite;
import org.jeecg.modules.activiti.entity.ActTaskUnite;

/**
 * @Description: 流程节点扩展表
 * @Author: jeecg-boot
 * @Date:   2020-03-30
 * @Version: V1.0
 */
public interface IActNodeService extends IService<ActNode> {

    ActNodeUnite saveOrUpdateUnite(String nodeId, String procDefId, String departId, String roleIds, String userIds, LoginUser user);

    void saveTaskUnite(String taskId, String taskKey, String procInstId, String procDefId, String owner,String assignee,int isStartUnite);
}

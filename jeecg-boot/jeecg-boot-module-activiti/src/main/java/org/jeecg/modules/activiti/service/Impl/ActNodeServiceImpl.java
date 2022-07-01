package org.jeecg.modules.activiti.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.activiti.entity.*;
import org.jeecg.modules.activiti.mapper.ActNodeMapper;
import org.jeecg.modules.activiti.service.IActNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 流程节点扩展表
 * @Author: pmc
 * @Date:   2020-03-30
 * @Version: V1.0
 */
@Service
public class ActNodeServiceImpl extends ServiceImpl<ActNodeMapper, ActNode> implements IActNodeService {


    @Autowired
    ISysBaseAPI sysBaseAPI;

    public List<String> getRoleByUserName(String username) {
        return this.baseMapper.getRoleByUserName(username);
    }
    public void deleteByNodeId(String id) {
        this.remove(new LambdaQueryWrapper<ActNode>().eq(ActNode::getNodeId,id));
    }

    public List<LoginUser> findUserByNodeId(String nodeId) {
        List<LoginUser> users = this.baseMapper.findUserByNodeId(nodeId);
        if (users.size()==0) users = Lists.newArrayList();
        return users;
    }

    public List<Role> findRoleByNodeId(String nodeId) {
        return this.baseMapper.findRoleByNodeId(nodeId);
    }

    public List<Role> findAppointRoleByNodeId(String nodeId){
        return this.baseMapper.findAppointRoleByNodeId(nodeId);
    }

    public List<Department> findDepartmentByNodeId(String nodeId) {
        return this.baseMapper.findDepartmentByNodeId(nodeId);
    }

    public List<Department> findAppointDepartmentByNodeId(String nodeId) {
        return this.baseMapper.findAppointDepartmentByNodeId(nodeId);
    }

    public String findRelateIdByNodeId(String nodeId) {
        return this.baseMapper.findRelateIdByNodeId(nodeId);
    }


    public ActNodeUnite findByNodeIdAndProDefId(String nodeId, String defId) {
        return this.baseMapper.findByNodeIdAndProDefId(nodeId,defId);
    }

    public ActNodeUnite findByNodeIdAndProDefId2(String nodeId, String defId) {
        ActNodeUnite unite = this.baseMapper.findByNodeIdAndProDefId(nodeId,defId);
        if(StringUtils.isNotBlank(unite.getUniteRoleIds())){
            String[] roleCodes =unite.getUniteRoleIds().split(",");

            List<String> roleIds = sysBaseAPI.getRoleIdsByCodes(roleCodes);
            unite.setUniteRoleIds(String.join(",",roleIds));
        }
        return unite;
    }

    public ActTaskUnite findTaskUnite(String taskId, String defId) {

        return this.baseMapper.findTaskUnite(taskId,defId);
    }

    public List<Role> findRolesByCodes(List<String> roleCodes){
        return this.baseMapper.findRolesByCodes(roleCodes);
    }

    public List<Department> findDepartmentsByIds(List<String> departIds) {
        return this.baseMapper.findDepartmentsByIds(departIds);
    }


    public Boolean hasChooseDepHeader(String nodeId) {
        List<ActNode> listNode = findByNodeIdAndType(nodeId, 4);
        if(listNode!=null&&listNode.size()>0){
            return true;
        }
        return false;
    }
    public Boolean hasChooseSponsor(String nodeId) {
        List<ActNode> listNode = findByNodeIdAndType(nodeId, 3);
        if(listNode!=null&&listNode.size()>0){
            return true;
        }
        return false;
    }

    public Boolean hasChooseDepRole(String nodeId) {
        List<ActNode> listNode = findByNodeIdAndType(nodeId, 5);
        if(listNode!=null&&listNode.size()>0){
            return true;
        }
        return false;
    }

    public Boolean hasChooseDepUser(String nodeId) {
        List<ActNode> listNode = findByNodeIdAndType(nodeId, 6);
        if(listNode!=null&&listNode.size()>0){
            return true;
        }
        return false;
    }

    public Boolean hasChooseCustomized(String nodeId) {
        List<ActNode> listNode = findByNodeIdAndType(nodeId, 7);
        if(listNode!=null&&listNode.size()>0){
            return true;
        }
        return false;
    }

    public List<ActNode> findByNodeIdAndType(String nodeId, int type) {
        return list(new LambdaQueryWrapper<ActNode>().eq(ActNode::getNodeId,nodeId).eq(ActNode::getType,type));
    }





    public List<String> findDepartmentIdsByNodeId(String nodeId) {
        List<Department> departmentByNodeId = this.findDepartmentByNodeId(nodeId);
        return departmentByNodeId.stream().map(Department::getId).distinct().collect(Collectors.toList());
    }

    public List<LoginUser> queryAllUser() {
        return this.baseMapper.queryAllUser();
    }

    public List<LoginUser> findUserByRoleId(String id) {
        return this.baseMapper.findUserByRoleId(id);
    }

    public List<LoginUser> findUserByRoleIdsAndDepartIds(HashMap<String,Object> hashMap){
        return this.baseMapper.findUserByRoleIdsAndDepartIds(hashMap);
    }


    public List<LoginUser> findUserDepartmentId(String id) {
        return this.baseMapper.findUserDepartmentId(id);
    }
    /**
     *
     * (下一个任务节点信息)
     * <br/> https://www.cnblogs.com/feiZhou/p/9344027.html
     * @param activityImpl
     *            流程节点信息
     * @param activityId
     *            当前流程节点Id信息
     * @param variables
     *            流程变量
     * @param processInstanceId
     *            流程实例ID
     * @return
     */
    public TaskDefinition nextTaskDefinition(ActivityImpl activityImpl, String activityId,
                                             Map<String, Object> variables, String processInstanceId) {

        PvmActivity ac = null;

        // 如果遍历节点为用户任务并且节点不是当前节点信息
        if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
            // 获取该节点下一个节点信息
            TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior())
                    .getTaskDefinition();
            return taskDefinition;

        } else if (!"endEvent".equals(activityImpl.getProperty("type"))) {
            // 当前节点是不结束节点的情况

            // 获取节点所有流向线路信息
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            List<PvmTransition> outTransitionsTemp = null;
            for (PvmTransition tr : outTransitions) {
                // 这里遍历的当然节点所有流程线的的终点节点
                ac = tr.getDestination(); // 获取线路的终点节点
                // 如果流向线路为排他网关
                if ("exclusiveGateway".equals(ac.getProperty("type"))) {
                    outTransitionsTemp = ac.getOutgoingTransitions();

                    // 如果排他网关只有一条线路信息
                    if (outTransitionsTemp.size() == 1) {
                        return nextTaskDefinition((ActivityImpl) outTransitionsTemp.get(0).getDestination(), activityId,
                                variables, processInstanceId);
                    } else if (outTransitionsTemp.size() > 1) { // 如果排他网关有多条线路信息

                        ActivityImpl nextActivityImpl = getNextActivityImplByLineCondition(outTransitionsTemp,
                                variables);
                        return nextTaskDefinition(nextActivityImpl, activityId, variables, processInstanceId);

                    }
                } else if ("userTask".equals(ac.getProperty("type"))) {// 下一个节点是用户任务
                    Boolean currenLineConditionResule = getCurrenLineConditionResule(tr, variables);
                    if (currenLineConditionResule) {
                        return ((UserTaskActivityBehavior) ((ActivityImpl) ac).getActivityBehavior())
                                .getTaskDefinition();
                    }
                }

            }
        }
        return null;
    }
    /**
     * (判断当前流程线是否满足流程参数条件)
     * @param outTransition
     * @param variables
     * @return
     */
    public Boolean getCurrenLineConditionResule(PvmTransition outTransition, Map<String, Object> variables) {

        boolean stringtoBoolean = true;
        Object conStr = outTransition.getProperty("conditionText"); // 获取线路判断条件信息
        // s: ${money>1000 or price >12}
        // variables (money=1100)
        // variables (price=100)
        // 去掉${和}
        String ElStr = "";
        if (conStr != null) {// 有条件
            ElStr = String.valueOf(conStr);
            ElStr = ElStr.substring(2, ElStr.length() - 1);
            try {
                stringtoBoolean = StringtoBoolean(ElStr, variables);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            return stringtoBoolean;
        } else {// 没有条件
            return stringtoBoolean;
        }
    }
    /**
     * (如果是排他网关判断,获取排他网关对应的用户任务节点)
     * @param outTransitionsTemp
     * @param variables
     * @return
     */
    public ActivityImpl getNextActivityImplByLineCondition(List<PvmTransition> outTransitionsTemp,
                                                           Map<String, Object> variables) {

        for (PvmTransition tr1 : outTransitionsTemp) {
            Object conStr = tr1.getProperty("conditionText"); // 获取线路判断条件信息
            // s: ${money>1000 or price >12}
            // variables (money=1100)
            // variables (price=100)
            // 去掉${和}
            String ElStr = "";
            if (conStr != null) {// 有条件
                ElStr = String.valueOf(conStr);
                ElStr = ElStr.substring(2, ElStr.length() - 1);
                try {
                    boolean stringtoBoolean = StringtoBoolean(ElStr, variables);
                    if (stringtoBoolean) {
                        return (ActivityImpl) tr1.getDestination();
                    }
                } catch (ScriptException e) {
                    continue;
                }
            } else {// 没有条件
                return (ActivityImpl) tr1.getDestination();
            }
        }
        return null;
    }
    /**
     * (字符串表达式计算逻辑值)
     * @param str
     * @param var
     * @return
     * @throws ScriptException
     */
    public static boolean StringtoBoolean(String str, Map<String,Object> var) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        Set<String> keys = var.keySet();
        for (String key : keys) {
            engine.put(key,var.get(key));
        }
        str= str.replaceAll(" or ", " || ").replaceAll(" and ", " && ");
        Object result = engine.eval(str);
        return Boolean.parseBoolean(String.valueOf(result));

    }

    public List<ActNode> findByNodeId(String nodeId) {
        return this.baseMapper.findByNodeId(nodeId);
    }

    public ActNode getOneBean(LambdaQueryWrapper<ActNode> wrapper){

        List<ActNode> actNodeList = this.list(wrapper);
        if(actNodeList.size()>0){
            return actNodeList.get(0);
        }else{
            return new ActNode();
        }
    }

    @Override
    public ActNodeUnite saveOrUpdateUnite(String nodeId, String procDefId, String departId, String roleIds,String userIds,LoginUser user) {

        ActNodeUnite unite = this.baseMapper.findByNodeIdAndProDefId(nodeId,procDefId);
        if(unite == null){
            unite = new ActNodeUnite();
            unite.setIsUnite(1);
            unite.setCreateBy(user.getUsername());
            unite.setNodeId(nodeId);
            unite.setUniteDepartId(departId);
            unite.setUniteRoleIds(roleIds);
            unite.setUniteUserIds(userIds);
            unite.setProcDefId(procDefId);

            this.baseMapper.saveUnite(unite);
        }else{
            if(StringUtils.isBlank(departId) && StringUtils.isBlank(roleIds) && StringUtils.isBlank(userIds)){
                unite.setIsUnite(0);
            }else{
                unite.setIsUnite(1);
            }
            unite.setUniteDepartId(departId);
            unite.setUniteRoleIds(roleIds);
            unite.setUniteUserIds(userIds);
            unite.setUpdateBy(user.getUsername());
            unite.setUpdateTime(new Date());
            this.baseMapper.updateUnite(unite);
        }

        return unite;
    }

    @Override
    public void saveTaskUnite(String taskId, String taskKey, String procInstId, String procDefId, String owner,String assignee,int isStartUnite) {
        ActTaskUnite taskUnite = new ActTaskUnite();
        taskUnite.setTaskId(taskId);
        taskUnite.setTaskKey(taskKey);
        taskUnite.setProcInstId(procInstId);
        taskUnite.setProcDefId(procDefId);
        taskUnite.setOwner(owner);
        taskUnite.setAssignee(assignee);
        taskUnite.setIsStartUnite(isStartUnite);
        taskUnite.setCreateBy(owner);
        this.baseMapper.saveTaskUnite(taskUnite);
    }
}

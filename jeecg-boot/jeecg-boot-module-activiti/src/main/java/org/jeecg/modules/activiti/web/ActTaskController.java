package org.jeecg.modules.activiti.web;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.activiti.entity.*;
import org.jeecg.modules.activiti.service.Impl.ActBusinessServiceImpl;
import org.jeecg.modules.activiti.service.Impl.ActNodeServiceImpl;
import org.jeecg.modules.activiti.service.Impl.ActZprocessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author pmc
 */
@Slf4j
@RestController
@RequestMapping("/actTask")
@Transactional
@Api(tags = "流程")
public class ActTaskController {

    @Autowired
    private TaskService taskService;


    @Autowired
    private HistoryService historyService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private ActZprocessServiceImpl actZprocessService;

    @Autowired
    private ActBusinessServiceImpl actBusinessService;
    @Autowired
    ISysBaseAPI sysBaseAPI;
    @Autowired
    private ActNodeServiceImpl actNodeService;

    /*代办列表*/
    @AutoLog(value = "流程-代办列表")
    @ApiOperation(value="流程-代办列表", notes="代办列表")
    @RequestMapping(value = "/todoList" ,method = RequestMethod.GET)
    public Result<Object> todoList(@ApiParam(value = "任务名称" )String name,
                                   @ApiParam(value = "任务分类" )String categoryId,
                                   @ApiParam(value = "优先级" )Integer priority,
                                   @ApiParam(value = "创建开始时间" )String createTime_begin,
                                   @ApiParam(value = "创建结束时间" )String createTime_end,
                                   HttpServletRequest request){
        List<TaskVo> list = new ArrayList<>();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getUsername();
        TaskQuery query = taskService.createTaskQuery().taskCandidateOrAssigned(userId);
        // 多条件搜索
        query.orderByTaskPriority().desc();
        query.orderByTaskCreateTime().desc();
        if(StrUtil.isNotBlank(name)){
            query.taskNameLike("%"+name+"%");
        }
        if(StrUtil.isNotBlank(categoryId)){
            query.taskCategory(categoryId);
        }
        if(priority!=null){
            query.taskPriority(priority);
        }
        if(StrUtil.isNotBlank(createTime_begin)){
            Date start = DateUtil.parse(createTime_begin);
            query.taskCreatedAfter(start);
        }
        if(StrUtil.isNotBlank(createTime_end)){
            Date end = DateUtil.parse(createTime_end);
            query.taskCreatedBefore(DateUtil.endOfDay(end));
        }
        //流程类型
        String type = request.getParameter("type");
        if (StrUtil.isNotBlank(type)){
            List<String> deployment_idList = actBusinessService.getBaseMapper().deployment_idListByType(type);
            if (deployment_idList.size()==0){
                query.deploymentIdIn(Lists.newArrayList(""));
            }else {
                query.deploymentIdIn(deployment_idList);
            }
        }
        String searchVal = request.getParameter("searchVal");
        if (StrUtil.isNotBlank(searchVal)){
            //搜索标题、申请人
            List<LoginUser> usersByName = sysBaseAPI.getUsersByName(searchVal);
            List<String> uNames = null;
            if (usersByName.size()==0){
                uNames = Lists.newArrayList("");
            }else {
                uNames = usersByName.stream().map(LoginUser::getUsername).collect(Collectors.toList());
            }
            List<ActBusiness> businessList = actBusinessService.list(new LambdaQueryWrapper<ActBusiness>()
                    .like(ActBusiness::getTitle, searchVal) //标题查询
                    .or().in(ActBusiness::getUserId,uNames)
            );
            if (businessList.size()>0){
                // 定义id
                List<String> pids = businessList.stream().filter(act -> act.getProcInstId()!=null).map(ActBusiness::getProcInstId).collect(Collectors.toList());
                query.processInstanceIdIn(pids);
            }else {
                query.processInstanceIdIn(Lists.newArrayList(""));
            }
        }
        List<Task> taskList = query.list();
        // 是否需要业务数据
        String needData = request.getParameter("needData");
        // 转换vo
        taskList.forEach(e -> {
            TaskVo tv = new TaskVo(e);

            // 关联委托人
            if(StrUtil.isNotBlank(tv.getOwner())){
                String realname = sysBaseAPI.getUserByName(tv.getOwner()).getRealname();
                tv.setOwner(realname);
            }
            List<IdentityLink> identityLinks = runtimeService.getIdentityLinksForProcessInstance(tv.getProcInstId());
            for(IdentityLink ik : identityLinks){
                // 关联发起人
                if("starter".equals(ik.getType())&&StrUtil.isNotBlank(ik.getUserId())){
                    tv.setApplyer(sysBaseAPI.getUserByName(ik.getUserId()).getRealname());
                }
            }
            // 关联流程信息
            ActZprocess actProcess = actZprocessService.getById(tv.getProcDefId());
            if(actProcess!=null){
                tv.setProcessName(actProcess.getName());
                tv.setRouteName(actProcess.getRouteName());
            }

            if(e.getDueDate() != null){
                tv.setDueTime(e.getDueDate());
            }

            //是否需要联合办理

            ActNodeUnite unite = actNodeService.findByNodeIdAndProDefId(tv.getKey(),tv.getProcDefId());


            if(unite == null){
                tv.setUnite(0);
            }else if (0 == unite.getIsUnite()){
                tv.setUnite(0);
            }else {
                tv.setUnite(unite.getIsUnite());
            }
            ActTaskUnite taskUnite = actNodeService.findTaskUnite(tv.getId(),tv.getProcDefId());
            if(taskUnite != null){
                tv.setStartUnite(taskUnite.getIsStartUnite());
            }

            // 关联业务key
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(tv.getProcInstId()).singleResult();
            tv.setBusinessKey(pi.getBusinessKey());
            ActBusiness actBusiness = actBusinessService.getById(pi.getBusinessKey());
            if(actBusiness!=null){
                tv.setTableId(actBusiness.getTableId());
                tv.setTableName(actBusiness.getTableName());
                tv.setTitle(actBusiness.getTitle());
                tv.setStatus(actBusiness.getStatus());
                tv.setResult(actBusiness.getResult());
                if (StrUtil.equals(needData,"true")){ // 需要业务数据
                    Map<String, Object> applyForm = actBusinessService.getApplyForm(actBusiness.getTableId(), actBusiness.getTableName());
                    tv.setDataMap(applyForm);
                }
            }
            list.add(tv);
        });
        return Result.ok(list);
    }
    /*获取可返回的节点*/
    @AutoLog(value = "流程-获取可返回的节点")
    @ApiOperation(value="流程-获取可返回的节点", notes="获取可返回的节点")
    @RequestMapping(value = "/getBackList", method = RequestMethod.GET)
    public Result<Object> getBackList(@RequestBody JSONObject jsonObject){
        String procInstId = jsonObject.getString("procInstId");
        List<HistoricTaskVo> list = new ArrayList<>();
        List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(procInstId)
                .finished().list();

        taskInstanceList.forEach(e -> {
            HistoricTaskVo htv = new HistoricTaskVo(e);
            list.add(htv);
        });

        // 去重
        LinkedHashSet<String> set = new LinkedHashSet<String>(list.size());
        List<HistoricTaskVo> newList = new ArrayList<>();
        list.forEach(e->{
            if(set.add(e.getName())){
                newList.add(e);
            }
        });

        return Result.ok(newList);
    }
    /*任务节点审批 驳回至发起人*/
    @AutoLog(value = "流程-任务节点审批 驳回至发起人")
    @ApiOperation(value="流程-任务节点审批 驳回至发起人", notes="任务节点审批 驳回至发起人")
//    @ApiParam("任务id") @RequestParam("id") String id,
//    @ApiParam("流程实例id") @RequestParam String procInstId,
//    @ApiParam("意见评论") @RequestParam(required = false) String comment,
//    @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
//    @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
//    @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail
    @RequestMapping(value = "/back", method = RequestMethod.POST)
    public Result<Object> back(@RequestBody JSONObject jsonObject){
        String procInstId = jsonObject.getString("procInstId");
        String id = jsonObject.getString("id");
        String comment = jsonObject.getString("comment");
        Boolean sendMessage = jsonObject.getBoolean("sendMessage");
        Boolean sendSms = jsonObject.getBoolean("sendSms");
        Boolean sendEmail = jsonObject.getBoolean("sendEmail");
        if(StrUtil.isBlank(comment)){
            comment = "";
        }
        taskService.addComment(id, procInstId, comment);

        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        // 删除流程实例
        runtimeService.deleteProcessInstance(procInstId, "backed");
        ActBusiness actBusiness = actBusinessService.getById(pi.getBusinessKey());
        actBusiness.setStatus(ActivitiConstant.STATUS_FINISH);
        actBusiness.setResult(ActivitiConstant.RESULT_FAIL);
        actBusinessService.updateById(actBusiness);
        // 异步发消息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        actZprocessService.sendMessage(actBusiness.getId(),sysUser,sysBaseAPI.getUserByName(actBusiness.getUserId()),ActivitiConstant.MESSAGE_BACK_CONTENT,
                String.format("您的 【%s】 申请已被驳回！",actBusiness.getTitle()),sendMessage, sendSms, sendEmail);
        // 记录实际审批人员
        actBusinessService.insertHI_IDENTITYLINK(IdUtil.simpleUUID(),
                ActivitiConstant.EXECUTOR_TYPE_b, sysUser.getUsername(), id, procInstId);
        //修改业务表的流程字段
        actBusinessService.updateBusinessStatus(actBusiness.getTableName(), actBusiness.getTableId(),"驳回");
        return Result.ok("操作成功");
    }
    /*流程流转历史*/
    @AutoLog(value = "流程-流程流转历史")
    @ApiOperation(value="流程-流程流转历史", notes="流程流转历史")
    @RequestMapping(value = "/historicFlow/{id}", method = RequestMethod.GET)
    public Result<Object> historicFlow(@ApiParam("实例Id")@PathVariable String id){

        List<HistoricTaskVo> list = new ArrayList<>();

        List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(id).orderByHistoricTaskInstanceEndTime().asc().list();

        // 转换vo
        taskList.forEach(e -> {
            HistoricTaskVo htv = new HistoricTaskVo(e);
            List<Assignee> assignees = new ArrayList<>();
            List<LoginUser> userList = new ArrayList<>();
            // 关联分配人（委托用户时显示该人）
            if(StrUtil.isNotBlank(htv.getAssignee())){

                String assignee = sysBaseAPI.getUserByName(htv.getAssignee()).getRealname();
                String owner = sysBaseAPI.getUserByName(htv.getOwner()).getRealname();
                assignees.add(new Assignee(assignee+"(受"+owner+"委托)", true));
            }
            List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForTask(e.getId());
            // 获取实际审批用户id



            List<String> userIds_b = actBusinessService.findUserIdByTypeAndTaskId(ActivitiConstant.EXECUTOR_TYPE_b, e.getId());
            List<String> userIds_p = actBusinessService.findUserIdByTypeAndTaskId(ActivitiConstant.EXECUTOR_TYPE_p, e.getId());
            //判断是否已经开启联合办理
            ActTaskUnite taskUnite = actNodeService.findTaskUnite(htv.getId(),htv.getProcDefId());
            if(taskUnite != null){
                userIds_p = actBusinessService.findUserIdListByTypeAndTaskId(ActivitiConstant.EXECUTOR_TYPE_p, e.getId());
            }

            //获取催办用户ID
            List<String> userIds_urgent = actBusinessService.findUserIdByTypeAndTaskId(ActivitiConstant.EXECUTOR_urge, e.getId());
            for(HistoricIdentityLink hik : identityLinks){
                // 关联候选用户（分配的候选用户审批人）
                if(ActivitiConstant.EXECUTOR_candidate.equals(hik.getType())&& StrUtil.isNotBlank(hik.getUserId())){
                    LoginUser u = sysBaseAPI.getUserByName(hik.getUserId());
                    String username = u.getRealname();
                    LoginUser user = new LoginUser();
                    user.setUsername(u.getUsername());
                    user.setRealname(u.getRealname());
                    Assignee assignee = new Assignee(username, false);
                    /*审批过的标记一下，前端标颜色用*/
                    if(CollectionUtil.contains(userIds_b,hik.getUserId()) || CollectionUtil.contains(userIds_p,hik.getUserId())){
                        assignee.setIsExecutor(true);
                    }
                    userList.add(user);
                    assignees.add(assignee);
                }

                List<String> statusList = new ArrayList<>();
                statusList.add(ActivitiConstant.EXECUTOR_urge);
                statusList.add(ActivitiConstant.EXECUTOR_unite);
                statusList.add(ActivitiConstant.EXECUTOR_unite_agree);
                statusList.add(ActivitiConstant.EXECUTOR_postpone);
                statusList.add(ActivitiConstant.EXECUTOR_supervise);
                if(statusList.contains(hik.getType())&& StrUtil.isNotBlank(hik.getUserId())){
                    LoginUser u = sysBaseAPI.getUserByName(hik.getUserId());
                    String username = u.getRealname();
                    Assignee assignee = new Assignee(username, false);
                    /*审批过的标记一下，前端标颜色用*/
                    if(CollectionUtil.contains(userIds_b,hik.getUserId())){
                        assignee.setIsExecutor(true);
                    }
                    assignees.add(assignee);
                }
            }
            htv.setAssignees(assignees);
            htv.setUsers(userList);
            // 关联审批意见
            List<Comment> comments = taskService.getTaskComments(htv.getId(), "comment");
            if(comments!=null&&comments.size()>0){
                htv.setComment(comments.get(0).getFullMessage());
            }
            list.add(htv);
        });
        return Result.ok(list);
    }


    @AutoLog(value = "流程-流程流转图")
    @ApiOperation(value="流程-流程流转历史图", notes="流程流转历史")
    @RequestMapping(value = "/historicProcessFlow", method = RequestMethod.GET)
    public Result<Object> historicProcessFlow(@ApiParam("实例Id")@RequestParam String procInstId,
                                              @ApiParam("流程id") @RequestParam String procDefId){

        List<HistoricTaskVo> taskVoList = new ArrayList<>();

        List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(procInstId).orderByTaskCreateTime().desc().list();

        // 转换vo
        taskList.forEach(e -> {
            HistoricTaskVo htv = new HistoricTaskVo(e);

            if(!htv.getDeleteReason().equals(ActivitiConstant.MESSAGE_URGENT_CONTENT) && !htv.getDeleteReason().equals(ActivitiConstant.MESSAGE_UNITE_CONTENT)){
                List<Assignee> assignees = new ArrayList<>();
                // 关联分配人（委托用户时显示该人）
                if(StrUtil.isNotBlank(htv.getAssignee())){

                    String assignee = sysBaseAPI.getUserByName(htv.getAssignee()).getRealname();

                    String owner = sysBaseAPI.getUserByName(htv.getOwner()).getRealname();
                    assignees.add(new Assignee(assignee+"(受"+owner+"委托)", true));
                }
                List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForTask(e.getId());
                // 获取实际审批用户id
                List<String> userIds_b = actBusinessService.findUserIdByTypeAndTaskId(ActivitiConstant.EXECUTOR_TYPE_b, e.getId());
                List<String> userIds_p = actBusinessService.findUserIdByTypeAndTaskId(ActivitiConstant.EXECUTOR_TYPE_p, e.getId());

                //判断是否已经开启联合办理
                ActTaskUnite taskUnite = actNodeService.findTaskUnite(htv.getId(),htv.getProcDefId());
                if(taskUnite != null){
                    userIds_p = actBusinessService.findUserIdListByTypeAndTaskId(ActivitiConstant.EXECUTOR_TYPE_p, e.getId());
                }

                for(HistoricIdentityLink hik : identityLinks){
                    // 关联候选用户（分配的候选用户审批人）
                    if(ActivitiConstant.EXECUTOR_candidate.equals(hik.getType())&& StrUtil.isNotBlank(hik.getUserId())){
                        String username = sysBaseAPI.getUserByName(hik.getUserId()).getRealname();
                        Assignee assignee = new Assignee(username, false);
                        /*审批过的标记一下，前端标颜色用*/
                        if(CollectionUtil.contains(userIds_b,hik.getUserId()) || CollectionUtil.contains(userIds_p,hik.getUserId())){
                            assignee.setIsExecutor(true);
                        }
                        assignees.add(assignee);
                    }

                    List<String> statusList = new ArrayList<>();
                    statusList.add(ActivitiConstant.EXECUTOR_urge);
                    statusList.add(ActivitiConstant.EXECUTOR_unite);
                    statusList.add(ActivitiConstant.EXECUTOR_unite_agree);
                    statusList.add(ActivitiConstant.EXECUTOR_postpone);
                    statusList.add(ActivitiConstant.EXECUTOR_supervise);
                    if(statusList.contains(hik.getType())&& StrUtil.isNotBlank(hik.getUserId())){
                        LoginUser u = sysBaseAPI.getUserByName(hik.getUserId());
                        String username = u.getRealname();
                        Assignee assignee = new Assignee(username, false);
                        /*审批过的标记一下，前端标颜色用*/
                        if(CollectionUtil.contains(userIds_b,hik.getUserId())){
                            assignee.setIsExecutor(true);
                        }
                        assignees.add(assignee);
                    }
                }
                htv.setAssignees(assignees);
                // 关联审批意见
                List<Comment> comments = taskService.getTaskComments(htv.getId(), "comment");
                if(comments!=null&&comments.size()>0){
                    htv.setComment(comments.get(0).getFullMessage());
                }
                taskVoList.add(htv);
            }


        });



        BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);

        List<ProcessNodeFlowVo> list = new ArrayList<>();

        List<Process> processes = bpmnModel.getProcesses();
        if(processes==null||processes.size()==0){
            return Result.ok();
        }
        for(Process process : processes){
            Collection<FlowElement> elements = process.getFlowElements();
            for(FlowElement element : elements){
                ProcessNodeFlowVo node = new ProcessNodeFlowVo();
                node.setProcDefId(procDefId);
                node.setId(element.getId());
                node.setTitle(element.getName());
                if(element instanceof StartEvent){
                    // 开始节点
                    node.setType(0);

                }else if(element instanceof UserTask){
                    // 用户任务
                    node.setType(1);
                    node.setCurrentNode(false);
                    if(taskVoList.size()>0){
                        HistoricTaskVo currentTask = taskVoList.get(0);
                        if(currentTask.getKey().equals(node.getId())){
                            node.setCurrentNode(true);
                        }
                        List<HistoricTaskVo> taskVos = taskVoList.stream().filter(s->StringUtils.isNumeric(s.getId()) && s.getKey().equals(node.getId())).collect(Collectors.toList());

                        if(taskVos.size()>0){
                            HistoricTaskVo vo = taskVos.get(0);
                            node.setStartTime(vo.getStartTime());
                            node.setEndTime(vo.getEndTime());
                            node.setAssignees(vo.getAssignees());
                            node.setRemark(vo.getDescription());
                            node.setDeleteReason(vo.getDeleteReason());
                        }
                    }


                }else if(element instanceof EndEvent){
                    // 结束
                    node.setType(2);
                }else{
                    // 排除其他连线或节点
                    continue;
                }
                list.add(node);
            }
        }
        list.sort(Comparator.comparing(ProcessNodeFlowVo::getType));
        return Result.ok(list);
    }

    @RequestMapping(value = "/pass", method = RequestMethod.POST)
    @AutoLog(value = "流程-任务节点审批通过")
    @ApiOperation(value = "任务节点审批通过")
//    @ApiParam("任务id") @RequestParam String id,
//    @ApiParam("流程实例id") @RequestParam String procInstId,
//    @ApiParam("下个节点审批人") @RequestParam(required = false) String assignees,
//    @ApiParam("优先级") @RequestParam(required = false) Integer priority,
//    @ApiParam("意见评论") @RequestParam(required = false) String comment,
//    @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
//    @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
//    @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail
    public Result<Object> pass(@RequestBody JSONObject jsonObject){
        String id = jsonObject.getString("id");
        String procInstId = jsonObject.getString("procInstId");
        String assignees = jsonObject.getString("assignees");
        String comment = jsonObject.getString("comment");
        Integer priority = jsonObject.getInteger("priority");
        Boolean sendMessage = jsonObject.getBoolean("sendMessage");
        Boolean sendSms = jsonObject.getBoolean("sendSms");
        Boolean sendEmail = jsonObject.getBoolean("sendEmail");
        if(StrUtil.isBlank(comment)){
            comment = "";
        }

        taskService.addComment(id, procInstId, comment);
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        Task task = taskService.createTaskQuery().taskId(id).singleResult();
        if(StrUtil.isNotBlank(task.getOwner())&&!("RESOLVED").equals(task.getDelegationState().toString())){
            // 未解决的委托任务 先resolve
            String oldAssignee = task.getAssignee();
            taskService.resolveTask(id);
            taskService.setAssignee(id, oldAssignee);
        }
        /*会签思路：
        act_hi_identitylink记录着审批历史 ActivitiConstant.EXECUTOR_TYPE_p 标识审批通过
        1、节点设置中增加人数字段，表示需要多少人通过这个任务节点才通过
        2、此处查询审批历史，查看当前节点的审批情况，符合预设的人数调用 taskService.complete(id); 完成该节点任务
        否则只记录审批数据，不完成该任务节点
        3、会有的问题：
            1、如此，审批过的人代办中还会看到这条任务，需要标识自己审批过，但是这条任务自己不能再审了  或 能再审，但是再审记得把之前审批过的记录删掉
            2、下一个节点的审批人只能最后通过的人选择才有效
            3、如果下一个节点是会签，指定下一节点的审批人的数量必须不小于节点预设数量
            其他问题，待暴露
          */
        /*完成任务*/
        taskService.complete(id);
        ActBusiness actBusiness = actBusinessService.getById(pi.getBusinessKey());
        //修改业务表的流程字段
        actBusinessService.updateBusinessStatus(actBusiness.getTableName(), actBusiness.getTableId(),"审批中-"+task.getTaskDefinitionKey()+"-"+task.getName());

        //


        task.getName();
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        actBusinessService.updateExtra(actBusiness.getId(),task.getTaskDefinitionKey(),loginUser.getOrgCode());

        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        // 判断下一个节点
        if(tasks!=null&&tasks.size()>0){
            for(Task t : tasks){
                if(StrUtil.isBlank(assignees)){
                    // 如果下个节点未分配审批人为空 取消结束流程
                    List<LoginUser> users = actZprocessService.getNode(t.getTaskDefinitionKey()).getUsers();
                    if(users==null||users.size()==0){
                        runtimeService.deleteProcessInstance(procInstId, "canceled-审批节点未分配审批人，流程自动中断取消");
                        actBusiness.setStatus(ActivitiConstant.STATUS_CANCELED);
                        actBusiness.setResult(ActivitiConstant.RESULT_TO_SUBMIT);
                        actBusinessService.updateById(actBusiness);
                        //修改业务表的流程字段
                        actBusinessService.updateBusinessStatus(actBusiness.getTableName(), actBusiness.getTableId(),"审批异常-"+task.getTaskDefinitionKey()+"-"+task.getName()+"-审批节点未分配审批人，流程自动中断取消");

                        break;
                    }else{
                        // 避免重复添加
                        List<String> list = actBusinessService.selectIRunIdentity(t.getId(), ActivitiConstant.EXECUTOR_candidate);
                        if(list==null||list.size()==0) {
                            // 分配了节点负责人分发给全部
                            for (LoginUser user : users) {
                                taskService.addCandidateUser(t.getId(), user.getUsername());
                                // 异步发消息
                                actZprocessService.sendActMessage(loginUser,user,actBusiness,task.getName(),  sendMessage, sendSms, sendEmail);
                            }
                            taskService.setPriority(t.getId(), task.getPriority());

                            //新增额外扩展数据表
                            actBusinessService.saveOtherExtra(actBusiness,t,users);
                        }
                    }
                }else{
                    // 避免重复添加
                    List<String> list = actBusinessService.selectIRunIdentity(t.getId(), ActivitiConstant.EXECUTOR_candidate);
                    if(list==null||list.size()==0) {

                        for(String assignee : assignees.split(",")){
                            taskService.addCandidateUser(t.getId(), assignee);
                            // 异步发消息
                            LoginUser user = sysBaseAPI.getUserByName(assignee);
                            actZprocessService.sendActMessage(loginUser,user,actBusiness,task.getName(),  sendMessage, sendSms, sendEmail);
                            taskService.setPriority(t.getId(), priority);
                        }
                        //新增额外扩展数据表
                        List<LoginUser> userList = sysBaseAPI.queryUserByNames(assignees.split(","));
                        if(userList.size()>0){
                            actBusinessService.saveOtherExtra(actBusiness,t,userList);
                        }

                    }
                }
                // 设置任务的截止日期
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(t.getCreateTime());
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + ActivitiConstant.PROCESS_DUE_DAY);
                Date dueTime = calendar.getTime();
                taskService.setDueDate(t.getId(),dueTime);
            }
        } else {
            actBusiness.setStatus(ActivitiConstant.STATUS_FINISH);
            actBusiness.setResult(ActivitiConstant.RESULT_PASS);
            actBusinessService.updateById(actBusiness);
            // 异步发消息
            LoginUser user = sysBaseAPI.getUserByName(actBusiness.getUserId());
            actZprocessService.sendMessage(actBusiness.getId(),loginUser,user,ActivitiConstant.MESSAGE_PASS_CONTENT,
                    String.format("您的 【%s】 申请已通过！",actBusiness.getTitle()),sendMessage, sendSms, sendEmail);

            //修改业务表的流程字段
            actBusinessService.updateBusinessStatus(actBusiness.getTableName(), actBusiness.getTableId(),"审批通过");
            actBusinessService.updateFormStatus(actBusiness.getTableName(), actBusiness.getTableId(),2);

        }
        // 记录实际审批人员
        actBusinessService.insertHI_IDENTITYLINK(IdUtil.simpleUUID(),
                ActivitiConstant.EXECUTOR_TYPE_p, loginUser.getUsername(), id, procInstId);
        return Result.ok("操作成功");
    }
    @RequestMapping(value = "/delegate", method = RequestMethod.POST)
    @ApiOperation(value = "委托他人代办")
    @AutoLog(value = "流程-委托他人代办")
//    @ApiParam("任务id") @RequestParam String id,
//    @ApiParam("委托用户id") @RequestParam String userId,
//    @ApiParam("流程实例id") @RequestParam String procInstId,
//    @ApiParam("意见评论") @RequestParam(required = false) String comment,
//    @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
//    @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
//    @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail
    public Result<Object> delegate(@RequestBody JSONObject jsonObject){
        String id = jsonObject.getString("id");
        String procInstId = jsonObject.getString("procInstId");
        String userId = jsonObject.getString("userId");
        String comment = jsonObject.getString("comment");
        Boolean sendMessage = jsonObject.getBoolean("sendMessage");
        Boolean sendSms = jsonObject.getBoolean("sendSms");
        Boolean sendEmail = jsonObject.getBoolean("sendEmail");
        if(StrUtil.isBlank(comment)){
            comment = "";
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        taskService.addComment(id, procInstId, comment);
        taskService.delegateTask(id, userId);
        taskService.setOwner(id, sysUser.getUsername());
        ActBusiness actBusiness = actBusinessService.getOneBean(new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getProcInstId, procInstId));

        LoginUser user = sysBaseAPI.getUserByName(userId);

        actBusinessService.saveExtraByDelegate(actBusiness,id,user);
        // 异步发消息
        actZprocessService.sendMessage(actBusiness.getId(),sysUser,user,ActivitiConstant.MESSAGE_DELEGATE_CONTENT,
                String.format("您有一个来自 %s 的委托需要处理！",sysUser.getRealname()),sendMessage, sendSms, sendEmail);

        return Result.ok("操作成功");
    }


    @RequestMapping(value = "/buildUnite", method = RequestMethod.POST)
    @ApiOperation(value = "联合办理")
    @AutoLog(value = "流程-联合办理")
//    @ApiParam("任务id") @RequestParam String id,
//    @ApiParam("委托用户id") @RequestParam String userId,
//    @ApiParam("流程实例id") @RequestParam String procInstId,
//    @ApiParam("意见评论") @RequestParam(required = false) String comment,
//    @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
//    @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
//    @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail
    public Result<Object> buildUnite(@RequestBody JSONObject jsonObject){

        String id = jsonObject.getString("id");
        String procInstId = jsonObject.getString("procInstId");
        String userId = jsonObject.getString("userId");
        String comment = jsonObject.getString("comment");
        Boolean sendMessage = jsonObject.getBoolean("sendMessage");
        Boolean sendSms = jsonObject.getBoolean("sendSms");
        Boolean sendEmail = jsonObject.getBoolean("sendEmail");
        if(StrUtil.isBlank(comment)){
            comment = "";
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //添加委托人
        //taskService.setOwner(id, sysUser.getUsername());

        ActBusiness actBusiness = actBusinessService.getOneBean(new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getProcInstId, procInstId));

        List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForTask(id);

        List<String> userIds = Arrays.asList(userId.split(","));

        //调用Arrays.asList()生产的List的add、remove方法时报异常，这是由Arrays.asList() 返回的市Arrays的内部类ArrayList，
        // 而不是java.util.ArrayList。Arrays的内部类ArrayList和java.util.ArrayList都是继承AbstractList，remove、add等方法
        // AbstractList中是默认throw UnsupportedOperationException而且不作任何操作

        List<String> uList = new ArrayList<>(userIds);
        //将自身添加进联合办理人员名单
        uList.add(sysUser.getUsername());


        //去除重复人员
        Set<String> staffsSet = new HashSet<>(uList);

        //获取原人员名单
        List<String> list = identityLinks.stream().map(HistoricIdentityLink::getUserId).collect(Collectors.toList());

        //删除原人员名单
        if(list.size()>0){
            for (int i = 0; i <list.size() ; i++) {
                taskService.deleteCandidateUser(id,list.get(i));
            }
        }

        List<LoginUser> userList = new ArrayList<>();
        //添加
        if(staffsSet.size()>0){

            Iterator<String> it = staffsSet.iterator();
            while (it.hasNext()) {
                String uId = it.next();
                if(StringUtils.isNotBlank(uId)){
                    //添加联合办理候选人
                    taskService.addCandidateUser(id, uId);
                    // 异步发消息
                    LoginUser u = sysBaseAPI.getUserByName(uId);
                    userList.add(u);
                    actZprocessService.sendMessage(actBusiness.getId(),sysUser,u,ActivitiConstant.MESSAGE_DELEGATE_CONTENT,
                            String.format("您有一个来自 %s 的联合办理需要处理！",sysUser.getRealname()),sendMessage, sendSms, sendEmail);
                }
            }
        }



        actBusinessService.updateExtraByUnite(actBusiness,id,userList);

        Date now = new Date();
        String hi_taskId = IdUtil.simpleUUID();
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(id).singleResult();
        actBusinessService.insertHI_TASKINST(hi_taskId,taskInstance.getProcessDefinitionId(),taskInstance.getTaskDefinitionKey(),
                taskInstance.getProcessInstanceId(),taskInstance.getExecutionId(),taskInstance.getName(),now,now,ActivitiConstant.UNITE_FLAG);

        actBusinessService.insertHI_IDENTITYLINK(IdUtil.simpleUUID(),
                ActivitiConstant.EXECUTOR_unite, sysUser.getUsername(), hi_taskId, procInstId);

        actBusinessService.insertHI_COMMENT(hi_taskId,"comment",now,sysUser.getUsername(),hi_taskId,procInstId,"AddComment",comment);

        Task task = taskService.createTaskQuery().taskId(id).singleResult();
        actBusinessService.saveTaskAway(sysUser.getUsername(),task,actBusiness);

        //新增启动联合办理表
        actNodeService.saveTaskUnite(id,taskInstance.getTaskDefinitionKey(),procInstId,taskInstance.getProcessDefinitionId(),sysUser.getUsername(),userId,1);

        return Result.ok("操作成功");
    }


    @RequestMapping(value = "/backToTask", method = RequestMethod.POST)
    @ApiOperation(value = "任务节点审批驳回至指定历史节点")
    @AutoLog(value = "流程-任务节点审批驳回至指定历史节点")
//    @ApiParam("任务id") @RequestParam String id,
//    @ApiParam("驳回指定节点key") @RequestParam String backTaskKey,
//    @ApiParam("流程实例id") @RequestParam String procInstId,
//    @ApiParam("流程定义id") @RequestParam String procDefId,
//    @ApiParam("节点审批人") @RequestParam(required = false) String assignees,
//    @ApiParam("优先级") @RequestParam(required = false) Integer priority,
//    @ApiParam("意见评论") @RequestParam(required = false) String comment,
//    @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
//    @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
//    @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail
    public Result<Object> backToTask(@RequestBody JSONObject jsonObject){

        String id = jsonObject.getString("id");
        String procInstId = jsonObject.getString("procInstId");
        String backTaskKey = jsonObject.getString("backTaskKey");
        String comment = jsonObject.getString("comment");
        String procDefId = jsonObject.getString("procDefId");
        String assignees = jsonObject.getString("assignees");
        Integer priority = jsonObject.getInteger("priority");
        Boolean sendMessage = jsonObject.getBoolean("sendMessage");
        Boolean sendSms = jsonObject.getBoolean("sendSms");
        Boolean sendEmail = jsonObject.getBoolean("sendEmail");

        if(StrUtil.isBlank(comment)){
            comment = "";
        }
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        taskService.addComment(id, procInstId, comment);
        // 取得流程定义
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(procDefId);
        // 获取历史任务的Activity
        ActivityImpl hisActivity = definition.findActivity(backTaskKey);
        // 实现跳转
        managementService.executeCommand(new JumpTask(procInstId, hisActivity.getId()));
        // 重新分配原节点审批人
        //ActBusiness actBusiness = actBusinessService.getOne(new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getProcInstId, procInstId).last("limit 1"));
        ActBusiness actBusiness = actBusinessService.getOneBean(new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getProcInstId, procInstId));

        Task task = taskService.createTaskQuery().taskId(id).singleResult();

        actBusinessService.updateExtra(actBusiness.getId(),task.getTaskDefinitionKey(),loginUser.getOrgCode());

        actBusinessService.saveTaskAway(loginUser.getUsername(),task,actBusiness);

        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if(tasks!=null&&tasks.size()>0){
            tasks.forEach(e->{
                for(String assignee:assignees.split(",")){
                    taskService.addCandidateUser(e.getId(), assignee);
                    // 异步发消息
                    actZprocessService.sendMessage(actBusiness.getId(),loginUser,sysBaseAPI.getUserByName(assignee),ActivitiConstant.MESSAGE_TODO_CONTENT
                    ,"您有一个任务待审批，请尽快处理！",sendMessage, sendSms, sendEmail);
                }
                if(priority!=null){
                    taskService.setPriority(e.getId(), priority);
                }
                List<LoginUser> userList = sysBaseAPI.queryUserByNames(assignees.split(","));
                if(userList.size()>0){
                    actBusinessService.updateExtraByBack(actBusiness,e,userList);
                }
            });
        }
        // 记录实际审批人员
        actBusinessService.insertHI_IDENTITYLINK(IdUtil.simpleUUID(),
                ActivitiConstant.EXECUTOR_TYPE_b, loginUser.getUsername(), id, procInstId);
        return Result.ok("操作成功");
    }



    @RequestMapping(value = "/urgentTask", method = RequestMethod.POST)
    @ApiOperation(value = "任务节点催办")
    @AutoLog(value = "流程-任务节点催办")
    public Result<Object> urgentTask(@ApiParam("任务id") @RequestParam String id,
                                     @ApiParam("流程实例id") @RequestParam String procInstId,
                                     @ApiParam("催办备注") @RequestParam(required = false) String comment,
                                     @ApiParam("节点审批人") @RequestParam(required = false) String assignees,
                                     @ApiParam("任务开始时间") @RequestParam(required = false) String createTime,
                                     @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
                                     @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
                                     @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail){

        if(StrUtil.isBlank(comment)){
            comment = "催办提醒";
        }
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        ActBusiness actBusiness = actBusinessService.getOneBean(new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getProcInstId, procInstId));
        Task task = taskService.createTaskQuery().taskId(id).singleResult();

        List<String> usernameList = new ArrayList<>();
        for(String assignee:assignees.split(",")){
            // 异步发消息
            String _name = sysBaseAPI.getUserByName(assignee).getRealname();
            actZprocessService.sendUrgentMessage(loginUser,sysBaseAPI.getUserByName(assignee),actBusiness,task.getName(),createTime,comment,sendMessage, sendSms, sendEmail);
            usernameList.add(_name);
        }

        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(id).singleResult();

        String hi_taskId = IdUtil.simpleUUID();
        Date now = new Date();
        actBusinessService.insertHI_TASKINST(hi_taskId,taskInstance.getProcessDefinitionId(),taskInstance.getTaskDefinitionKey(),
                taskInstance.getProcessInstanceId(),taskInstance.getExecutionId(),taskInstance.getName(),now,now,ActivitiConstant.URGENT_FLAG);
        // 记录实际催办人员
        //taskService.addComment(hi_taskId,procInstId, comment);
        actBusinessService.insertHI_IDENTITYLINK(IdUtil.simpleUUID(),
                ActivitiConstant.EXECUTOR_urge, loginUser.getUsername(), hi_taskId, procInstId);
        //记录批注信息
        //taskService.addComment(hi_taskId, procInstId, comment);
        String string2 = usernameList.stream().collect(Collectors.joining(","));
        String message = "["+string2+"]["+comment+"]";
        actBusinessService.insertHI_COMMENT(hi_taskId,"comment",now,loginUser.getUsername(),hi_taskId,procInstId,"AddComment",message);
        return Result.ok("已催办");
    }


    @RequestMapping(value = "/awayTask", method = RequestMethod.POST)
    @ApiOperation(value = "查看并接收节点")
    @AutoLog(value = "流程-查看并接收节点")
    public Result<Object> awayTask(@ApiParam("任务id") @RequestParam String id,
                                   @ApiParam("流程实例id") @RequestParam String procInstId){

        ActBusiness actBusiness = actBusinessService.getOneBean(new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getProcInstId, procInstId));
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Task task = taskService.createTaskQuery().taskId(id).singleResult();

        List<ActHiTaskAway> awayList = actBusinessService.findTaskAway(actBusiness.getId(),task.getId(),loginUser.getUsername());
        List<String> userIds_c = actBusinessService.findUserIdListByTypeAndTaskId(ActivitiConstant.EXECUTOR_candidate, task.getId());

        if(userIds_c.contains(loginUser.getUsername()) && awayList.size() < 1){
            actBusinessService.saveTaskAway(loginUser.getUsername(),task,actBusiness);
        }

        return Result.ok("已接受工单");
    }

    @RequestMapping(value = "/superviseTask", method = RequestMethod.POST)
    @ApiOperation(value = "任务节点督办")
    @AutoLog(value = "流程-任务节点督办")
    public Result<Object> superviseTask(@ApiParam("任务id") @RequestParam String id,
                                     @ApiParam("流程实例id") @RequestParam String procInstId,
                                     @ApiParam("督办备注") @RequestParam(required = false) String comment,
                                     @ApiParam("节点审批人") @RequestParam(required = false) String assignees,
                                     @ApiParam("任务开始时间") @RequestParam(required = false) String createTime,
                                     @ApiParam("督办日期") @RequestParam(required = false) String dueTime,
                                     @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
                                     @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
                                     @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail){

        if(StrUtil.isBlank(comment)){
            comment = "督办提醒";
        }
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        ActBusiness actBusiness = actBusinessService.getOneBean(new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getProcInstId, procInstId));
        Task task = taskService.createTaskQuery().taskId(id).singleResult();
        Date old = task.getDueDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String remark = "";
        //督办逾期日期
        try {

            Date endTime = format.parse(dueTime);
            taskService.setDueDate(id,endTime);
            remark += "逾期日期变更为"+dueTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<String> usernameList = new ArrayList<>();
        for(String assignee:assignees.split(",")){
            // 异步发消息
            String _name = sysBaseAPI.getUserByName(assignee).getRealname();
            actZprocessService.sendSuperviseMessage(loginUser,sysBaseAPI.getUserByName(assignee),actBusiness,task.getName(),createTime,comment,sendMessage, sendSms, sendEmail);
            usernameList.add(_name);
            //记录督办记录表

            ActHiTaskSupervise supervise = new ActHiTaskSupervise();
            supervise.setBusinessId(actBusiness.getId());
            supervise.setTaskId(task.getId());
            supervise.setCreateBy(loginUser.getUsername());
            supervise.setOldDueTime(old);
            supervise.setNowDueTime(task.getDueDate());
            supervise.setSuperviseUserId(assignee);
            actBusinessService.saveTaskSupervise(supervise);
        }

        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(id).singleResult();

        String hi_taskId = IdUtil.simpleUUID();
        Date now = new Date();
        actBusinessService.insertHI_TASKINST(hi_taskId,taskInstance.getProcessDefinitionId(),taskInstance.getTaskDefinitionKey(),
                taskInstance.getProcessInstanceId(),taskInstance.getExecutionId(),taskInstance.getName(),now,now,ActivitiConstant.SUPERVISE_FLAG);


        // 记录实际催办人员
        //taskService.addComment(hi_taskId,procInstId, comment);
        actBusinessService.insertHI_IDENTITYLINK(IdUtil.simpleUUID(),
                ActivitiConstant.EXECUTOR_supervise, loginUser.getUsername(), hi_taskId, procInstId);
        //记录批注信息
        //taskService.addComment(hi_taskId, procInstId, comment);
        String string2 = usernameList.stream().collect(Collectors.joining(","));
        String message = "["+string2+"]["+comment+"]["+remark+"]";
        actBusinessService.insertHI_COMMENT(hi_taskId,"comment",now,loginUser.getUsername(),hi_taskId,procInstId,"AddComment",message);
        return Result.ok("已督办");
    }



    public class JumpTask implements Command<ExecutionEntity> {

        private String procInstId;
        private String activityId;

        public JumpTask(String procInstId, String activityId) {
            this.procInstId = procInstId;
            this.activityId = activityId;
        }

        @Override
        public ExecutionEntity execute(CommandContext commandContext) {

            ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findExecutionById(procInstId);
            executionEntity.destroyScope("backed");
            ProcessDefinitionImpl processDefinition = executionEntity.getProcessDefinition();
            ActivityImpl activity = processDefinition.findActivity(activityId);
            executionEntity.executeActivity(activity);

            return executionEntity;
        }

    }




    /*已办列表*/
    @AutoLog(value = "流程-已办列表")
    @ApiOperation(value="流程-已办列表", notes="已办列表")
    @RequestMapping(value = "/doneList" ,method = RequestMethod.GET)
    public Result<Object> doneList(String name,
                                   String categoryId,
                                   Integer priority,
                                   HttpServletRequest req){

        List<HistoricTaskVo> list = actBusinessService.getHistoricTaskVos(req, name, categoryId, priority);
        return Result.ok(list);
    }

    /*删除任务历史*/
    @AutoLog(value = "流程-删除任务历史")
    @ApiOperation(value="流程-删除任务历史", notes="删除任务历史")
    @RequestMapping(value = "/deleteHistoric/{ids}", method = RequestMethod.POST)
    public Result<Object> deleteHistoric( @PathVariable String ids){

        for(String id : ids.split(",")){
            historyService.deleteHistoricTaskInstance(id);
        }
        return Result.ok("操作成功");
    }



    @AutoLog(value = "流程-获取节点联合办理信息")
    @ApiOperation(value="流程-获取节点联合办理信息", notes="获取节点联合办理信息")
    @RequestMapping(value = "/getNodeUnite", method = RequestMethod.GET)
//    @ApiParam("任务id") @RequestParam String id,
//    @ApiParam("流程id") @RequestParam String procDefId
    public Result<Object> getNodeUnite(@ApiParam("任务id") @RequestParam String id,
                                       @ApiParam("流程id") @RequestParam String procDefId){
//        String id = jsonObject.getString("id");
//        String procDefId = jsonObject.getString("procDefId");
        ActNodeUnite unite = actNodeService.findByNodeIdAndProDefId2(id,procDefId);
        return Result.ok(unite);
    }


    @RequestMapping(value = "/isLastPassUnite", method = RequestMethod.GET)
    @ApiOperation(value = "是否最后一个通过联合办理")
    @AutoLog(value = "流程-是否最后一个通过联合办理")
//    @ApiParam("任务id") @RequestParam String id
    public Result<Object> isLastPassUnite(@RequestBody JSONObject jsonObject ){
        String id = jsonObject.getString("id");
        //ActBusiness actBusiness = actBusinessService.getOneBean(new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getProcInstId, procInstId));
        Task task = taskService.createTaskQuery().taskId(id).singleResult();

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 获取实际审批用户id
        List<String> userIds_c = actBusinessService.findUserIdListByTypeAndTaskId(ActivitiConstant.EXECUTOR_candidate, task.getId());
        List<String> userIds_p = actBusinessService.findUserIdListByTypeAndTaskId(ActivitiConstant.EXECUTOR_TYPE_p, task.getId());


        if(userIds_c.size()>0){
            List<String> remain =new ArrayList<>();

            HashSet<String> hs1 = new HashSet<>(userIds_c);
            HashSet<String> hs2 = new HashSet<>(userIds_p);
            hs1.removeAll(hs2);

            remain.addAll(hs1);

            if(remain.contains(sysUser.getUsername())){
                //仅剩1人则表示该账号是最后一个
                if(remain.size() == 1){
                    return Result.ok(1);
                }else{
                    return Result.ok(0);
                }
            }else{
                return Result.error("你已经通过审核或没有权限审核");
            }
        }

        return Result.ok();
    }

    @RequestMapping(value = "/passUnite", method = RequestMethod.POST)
    @AutoLog(value = "流程-联合办理通过")
    @ApiOperation(value = "联合办理通过")
//    @ApiParam("任务id") @RequestParam String id,
//    @ApiParam("流程实例id") @RequestParam String procInstId,
//
//    @ApiParam("意见评论") @RequestParam(required = false) String comment,
//    @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
//    @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
//    @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail
    public Result<Object> passUnite(@RequestBody JSONObject jsonObject){
        /*会签思路：
        act_hi_identitylink记录着审批历史 ActivitiConstant.EXECUTOR_TYPE_p 标识审批通过
        1、节点设置中增加人数字段，表示需要多少人通过这个任务节点才通过
        2、此处查询审批历史，查看当前节点的审批情况，符合预设的人数调用 taskService.complete(id); 完成该节点任务
        否则只记录审批数据，不完成该任务节点
        3、会有的问题：
            1、如此，审批过的人代办中还会看到这条任务，需要标识自己审批过，但是这条任务自己不能再审了  或 能再审，但是再审记得把之前审批过的记录删掉
            2、下一个节点的审批人只能最后通过的人选择才有效
            3、如果下一个节点是会签，指定下一节点的审批人的数量必须不小于节点预设数量
            其他问题，待暴露
          */
        /*完成任务*/
        String id = jsonObject.getString("id");
        String procInstId = jsonObject.getString("procInstId");
        String comment = jsonObject.getString("comment");
        Boolean sendMessage = jsonObject.getBoolean("sendMessage");
        Boolean sendSms = jsonObject.getBoolean("sendSms");
        Boolean sendEmail = jsonObject.getBoolean("sendEmail");

        Task taskInstance = taskService.createTaskQuery().taskId(id).singleResult();

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 获取实际审批用户id
        List<String> userIds_c = actBusinessService.findUserIdListByTypeAndTaskId(ActivitiConstant.EXECUTOR_candidate, taskInstance.getId());
        List<String> userIds_p = actBusinessService.findUserIdListByTypeAndTaskId(ActivitiConstant.EXECUTOR_TYPE_p, taskInstance.getId());
        if(userIds_c.size()>0){

            HashSet<String> hs1 = new HashSet<>(userIds_c);
            HashSet<String> hs2 = new HashSet<>(userIds_p);
            hs1.removeAll(hs2);

            List<String> remain = new ArrayList<>(hs1);

            if(remain.contains(sysUser.getUsername())){
                //仅剩1人则表示该账号是最后一个
                if(remain.size() == 1){
                    return Result.error("该流程有变动,请重新刷新页面");
                }else{
                    String hi_taskId = IdUtil.simpleUUID();
                    Date now = new Date();
                    actBusinessService.insertHI_TASKINST(hi_taskId,taskInstance.getProcessDefinitionId(),taskInstance.getTaskDefinitionKey(),
                            taskInstance.getProcessInstanceId(),taskInstance.getExecutionId(),taskInstance.getName(),now,now,ActivitiConstant.UNITE_FLAG);


                    //taskService.setDueDate(hi_taskId,now);
                    actBusinessService.insertHI_IDENTITYLINK(IdUtil.simpleUUID(),
                            ActivitiConstant.EXECUTOR_unite_agree, sysUser.getUsername(), hi_taskId, procInstId);

                    actBusinessService.insertHI_IDENTITYLINK(IdUtil.simpleUUID(),
                            ActivitiConstant.EXECUTOR_TYPE_p, sysUser.getUsername(), id, procInstId);

                    actBusinessService.insertHI_COMMENT(hi_taskId,"comment",now,sysUser.getUsername(),hi_taskId,procInstId,"AddComment",comment);

                }
            }else{
                return Result.error("你已经通过审核或没有权限审核");
            }
        }


        actBusinessService.insertHI_IDENTITYLINK(IdUtil.simpleUUID(),
                ActivitiConstant.EXECUTOR_TYPE_p, sysUser.getUsername(), id, procInstId);
        return Result.ok("操作成功");
    }

    @RequestMapping(value = "/postpone", method = RequestMethod.POST)
    @AutoLog(value = "流程-申请延期")
    @ApiOperation(value = "申请延期")
//    @ApiParam("任务id") @RequestParam String id,
//    @ApiParam("流程实例id") @RequestParam String procInstId,
//    @ApiParam("延期原因") @RequestParam(required = false) String comment,
//    @ApiParam("延期日期") @RequestParam(required = true) String dueTime,
//    @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
//    @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
//    @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail
    public Result<Object> postpone(@RequestBody JSONObject jsonObject){

        String id = jsonObject.getString("id");
        String procInstId = jsonObject.getString("procInstId");
        String dueTime = jsonObject.getString("dueTime");
        String comment = jsonObject.getString("comment");
        Boolean sendMessage = jsonObject.getBoolean("sendMessage");
        Boolean sendSms = jsonObject.getBoolean("sendSms");
        Boolean sendEmail = jsonObject.getBoolean("sendEmail");
        Task task = taskService.createTaskQuery().taskId(id).singleResult();

        // 申请延期
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date endTime = format.parse(dueTime);
            taskService.setDueDate(task.getId(),endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();

        ActBusiness actBusiness = actBusinessService.getById(pi.getBusinessKey());

        // 修改流程标记
        actBusiness.setPostponeFlag(1);
        actBusinessService.updateById(actBusiness);

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //添加延期记录
        String hi_taskId = IdUtil.simpleUUID();
        Date now = new Date();
        actBusinessService.insertHI_TASKINST(hi_taskId,task.getProcessDefinitionId(),task.getTaskDefinitionKey(),
                task.getProcessInstanceId(),task.getExecutionId(),task.getName(),now,now,ActivitiConstant.POSTPONE_FLAG);

        actBusinessService.insertHI_IDENTITYLINK(IdUtil.simpleUUID(),
                ActivitiConstant.EXECUTOR_postpone, sysUser.getUsername(), hi_taskId, procInstId);

        actBusinessService.insertHI_COMMENT(hi_taskId,"comment",now,sysUser.getUsername(),hi_taskId,procInstId,"AddComment",comment);

        return Result.ok("延期成功");
    }
}

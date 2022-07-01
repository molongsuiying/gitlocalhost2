package org.jeecg.modules.activiti.web;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.activiti.entity.*;
import org.jeecg.modules.activiti.service.Impl.ActBusinessServiceImpl;
import org.jeecg.modules.activiti.service.Impl.ActZprocessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *@author PanMeiCheng
 *@date 2020-04-02
 *@version 1.0
 */
@RestController
@RequestMapping("/actBusiness")
@Slf4j
@Transactional
@Api(tags="流程")
public class ActBusinessController {
    @Autowired
    ActBusinessServiceImpl actBusinessService;
    @Autowired
    ActZprocessServiceImpl actZprocessService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    ISysBaseAPI sysBaseAPI;
    /*添加申请草稿状态*/
    @AutoLog(value = "流程-添加申请草稿状态")
    @ApiOperation(value="流程-添加申请草稿状态", notes="业务表单参数数据一并传过来！")
    @PostMapping(value = "/add")
    public Result add(@ApiParam(value = "流程定义Id" ,required = true) @RequestParam("procDefId") String procDefId,
                      @ApiParam(value = "申请标题" ,required = true) @RequestParam("procDeTitle") String procDeTitle,
                      @ApiParam(value = "数据表名" ,required = true)@RequestParam("tableName") String tableName,
                       HttpServletRequest request){
        /*保存业务表单数据到数据库表*/
        String tableId = IdUtil.simpleUUID();
        //如果前端上传了id
        String id = request.getParameter("id");
        if( id != null && !id.equals("")){
            tableId = id;
        }
        boolean isNew = actBusinessService.saveApplyForm(tableId, request);
        ActBusiness actBusiness = new ActBusiness();
        if (isNew){
            // 新增数据 保存至我的申请业务
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            String username = sysUser.getUsername();
            actBusiness.setId(UUIDGenerator.generate());
            actBusiness.setUserId(username);
            actBusiness.setTableId(tableId);
            actBusiness.setProcDefId(procDefId);
            String title = request.getParameter(ActivitiConstant.titleKey);
            if (StrUtil.isNotBlank(title)){
                actBusiness.setTitle(title);
            }else {
                actBusiness.setTitle(procDeTitle);
            }
            actBusiness.setTableName(tableName);
            actBusinessService.save(actBusiness);
        } else {
            //actBusiness = actBusinessService.getOne(new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getTableId,tableId).last("limit 1"));
            actBusiness = actBusinessService.getOneBean(new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getTableId,tableId));

        }
        Map<String,String> map = new HashMap<>();
        map.put("id",actBusiness.getId());
        return Result.ok(map);
    }
    /*获取业务表单信息*/
    @AutoLog(value = "流程-获取业务表单信息")
    @ApiOperation(value="流程-获取业务表单信息", notes="获取业务表单信息")
    @RequestMapping(value = "/getForm", method = RequestMethod.GET)
    public Result getForm(@ApiParam(value = "业务表数据id" ,required = true)@RequestParam("tableId") String tableId,
                          @ApiParam(value = "业务表名" ,required = true) @RequestParam("tableName") String tableName){
        if (StrUtil.isBlank(tableName)){
            return Result.error("参数缺省！");
        }
        Map<String, Object> applyForm = actBusinessService.getApplyForm(tableId, tableName);
        return Result.ok(applyForm);
    }
    /*修改业务表单信息*/
    @AutoLog(value = "流程-修改业务表单信息")
    @ApiOperation(value="流程-修改业务表单信息", notes="业务表单参数数据一并传过来!")
    @RequestMapping(value = "/editForm", method = RequestMethod.POST)
    public Result editForm(@ApiParam(value = "业务表数据id" ,required = true) @RequestParam("id") String id,
                           HttpServletRequest request){
        /*保存业务表单数据到数据库表*/
        actBusinessService.saveApplyForm(id,request);
        return Result.ok();
    }
    /*通过id删除草稿状态申请*/
    @AutoLog(value = "流程-通过id删除草稿状态申请")
    @ApiOperation(value="流程-通过id删除草稿状态申请", notes="通过id删除草稿状态申请")
    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    public Result delByIds(@ApiParam(value = "流程扩展表id，多个,号相连" ,required = true)  @RequestBody JSONObject jsonObject){
        String ids = jsonObject.getString("ids");
        for(String id : ids.split(",")){
            ActBusiness actBusiness = actBusinessService.getById(id);
            if(actBusiness.getStatus()!=ActivitiConstant.STATUS_TO_APPLY){
                return Result.error("删除失败, 仅能删除草稿状态的申请");
            }
            // 删除关联业务表
            //actBusinessService.deleteBusiness(actBusiness.getTableName(), actBusiness.getTableId());
            // 修改关联业务表状态
            actBusinessService.updateFormStatus(actBusiness.getTableName(), actBusiness.getTableId(),0);
            actBusinessService.removeById(id);
        }
        return Result.ok("删除成功");
    }
    /*提交申请 启动流程*/
    @AutoLog(value = "流程-提交申请 启动流程")
    @ApiOperation(value="流程-提交申请 启动流程", notes="提交申请 启动流程。")
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public Result apply(@RequestBody ActBusiness act){
        ActBusiness actBusiness = actBusinessService.getById(act.getId());
        if(actBusiness==null){
            return Result.error("actBusiness表中该id不存在");
        }
        String tableId = actBusiness.getTableId();
        String tableName = actBusiness.getTableName();

        act.setTableId(tableId);
        Map<String, Object> busiData = actBusinessService.getBusiData(tableId, tableName);

        if (MapUtil.isNotEmpty(busiData)&&busiData.get(ActivitiConstant.titleKey)!=null){
            //如果表单里有 标题  更新一下
            actBusiness.setTitle(busiData.get(ActivitiConstant.titleKey)+"");
        }

        String processInstanceId = actZprocessService.startProcess(act);
        actBusiness.setProcInstId(processInstanceId);
        actBusiness.setStatus(ActivitiConstant.STATUS_DEALING);
        actBusiness.setResult(ActivitiConstant.RESULT_DEALING);
        actBusiness.setApplyTime(new Date());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        actBusiness.setApplyBy(sysUser.getUsername());
        actBusinessService.updateById(actBusiness);
        //修改业务表的流程字段
        actBusinessService.updateBusinessStatus(actBusiness.getTableName(), actBusiness.getTableId(),"启动");

        actBusiness.setAssignees(act.getAssignees());
        if(1 == act.getReFlag()){
            actBusiness.setReFlag(act.getReFlag());
        }else{
            actBusiness.setReFlag(0);
        }

        //添加数据表
        actBusinessService.saveFirstBusinessExtra(actBusiness,false);
        return Result.ok("操作成功");
    }

    /**
     *
     * @param jsonObject
     * @return
     */
    /*添加申请草稿状态*/
    @AutoLog(value = "流程-添加并申请")
    @ApiOperation(value="流程-添加并申请", notes="")
    @PostMapping(value = "/addAndApply")
    public Result addAndApply(@RequestBody JSONObject jsonObject){
        //流程定义ID
        String procDefId = jsonObject.getString("procDefId");
        //申请标题
        String procDeTitle = jsonObject.getString("procDeTitle");
        //数据表名
        String tableName = jsonObject.getString("tableName");
        //数据表ID
        String tableId = jsonObject.getString("tableId");

        ActBusiness actBusiness = new ActBusiness();
        // 新增数据 保存至我的申请业务
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String username = sysUser.getUsername();
        actBusiness.setId(UUIDGenerator.generate());

        actBusiness.setUserId(username);
        actBusiness.setTableId(tableId);
        actBusiness.setProcDefId(procDefId);
        String title = jsonObject.getString(ActivitiConstant.titleKey);
        if (StrUtil.isNotBlank(title)){
            actBusiness.setTitle(title);
        }else {
            actBusiness.setTitle(procDeTitle);
        }
        actBusiness.setTableName(tableName);
        actBusinessService.save(actBusiness);
        Map<String,String> map = new HashMap<>();
        map.put("id",actBusiness.getId());

        actBusinessService.updateFormStatus(tableName,tableId,1);
        return Result.ok(map);
    }


    /*火速处理*/
    @AutoLog(value = "流程-火速处理")
    @ApiOperation(value="流程-火速处理", notes="")
    @PostMapping(value = "/applyFinish")
    public Result applyFinish(//@ApiParam(value = "流程定义Id" ,required = true) String procDefId,
                              //@ApiParam(value = "申请标题" ,required = true) String procDeTitle,
                              //@ApiParam(value = "数据表名" ,required = true) String tableName,
                              @ApiParam(value = "数据表ID" ,required = true) @RequestParam("tableId") String tableId,
                              @ApiParam(value = "处理意见" ,required = false)@RequestParam("comment") String comment,
                              @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
                              @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
                              @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail,
                              HttpServletRequest request){


        ActZprocess actZprocess = actZprocessService.getOne(new LambdaQueryWrapper<ActZprocess>().eq(ActZprocess::getTypeId, ActivitiConstant.HANDLE_QUICK));

        String procDeTitle = actZprocess.getName();
        String procDefId = actZprocess.getId();
        String tableName = actZprocess.getBusinessTable();
        ActBusiness actBusiness = new ActBusiness();
        // 新增数据 保存至我的申请业务
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String username = sysUser.getUsername();
        actBusiness.setId(UUIDGenerator.generate());
        actBusiness.setUserId(username);
        actBusiness.setTableId(tableId);
        actBusiness.setProcDefId(procDefId);
        String title = request.getParameter(ActivitiConstant.titleKey);
        if (StrUtil.isNotBlank(title)){
            actBusiness.setTitle(title);
        }else {
            actBusiness.setTitle(procDeTitle);
        }
        actBusiness.setTableName(tableName);
        actBusinessService.save(actBusiness);
        Map<String,String> map = new HashMap<>();
        map.put("id",actBusiness.getId());

        //更新申诉表单状态
        actBusinessService.updateFormStatus(tableName,tableId,1);
        actBusiness.setAssignees(username);
        //启动流程
        String processInstanceId = actZprocessService.startProcess(actBusiness);
        actBusiness.setProcInstId(processInstanceId);
        actBusiness.setStatus(ActivitiConstant.STATUS_DEALING);
        actBusiness.setResult(ActivitiConstant.RESULT_DEALING);
        actBusiness.setApplyTime(new Date());
        actBusiness.setApplyBy(username);
        actBusinessService.updateById(actBusiness);
        //修改业务表的流程字段
        actBusinessService.updateBusinessStatus(actBusiness.getTableName(), actBusiness.getTableId(),"启动");

        //添加数据表
        if(null == actBusiness.getReFlag()){
            actBusiness.setReFlag(0);
        }else{
            actBusiness.setReFlag(actBusiness.getReFlag());
        }
        actBusinessService.saveFirstBusinessExtra(actBusiness,true);

        if(StrUtil.isBlank(comment)){
            comment = "";
        }

        int priority = 0;

        //查找Task
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).processDefinitionId(procDefId).singleResult();

        String id = task.getId();
        taskService.addComment(id, processInstanceId, comment);
        if(StrUtil.isNotBlank(task.getOwner())&&!("RESOLVED").equals(task.getDelegationState().toString())){
            // 未解决的委托任务 先resolve
            String oldAssignee = task.getAssignee();
            taskService.resolveTask(id);
            taskService.setAssignee(id, oldAssignee);
        }
        /*完成任务*/
        taskService.complete(id);

        actBusinessService.saveTaskAway(sysUser.getUsername(),task,actBusiness);

        //修改业务表的流程字段
        actBusinessService.updateBusinessStatus(actBusiness.getTableName(), actBusiness.getTableId(),"审批中-"+task.getTaskDefinitionKey()+"-"+task.getName());

        task.getName();
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();

        // 判断下一个节点
        if(tasks!=null&&tasks.size()>0){
            for(Task t : tasks){
                List<String> list = actBusinessService.selectIRunIdentity(t.getId(), ActivitiConstant.EXECUTOR_candidate);
                if(list==null||list.size()==0) {

                    taskService.addCandidateUser(t.getId(), loginUser.getUsername());
                    taskService.setPriority(t.getId(), priority);
                }
            }
        } else {
            actBusiness.setStatus(ActivitiConstant.STATUS_FINISH);
            actBusiness.setResult(ActivitiConstant.RESULT_PASS);
            actBusinessService.updateById(actBusiness);

            //异步发消息 - 查找投诉表单的创建人
            Map<String, Object> formData = actBusinessService.getBusiData(tableId, tableName);
            Object createBy = formData.get("createBy");
            if(StringUtils.isNotBlank(createBy.toString())){
                LoginUser formUser = sysBaseAPI.getUserByName(createBy.toString());

                //LoginUser user = sysBaseAPI.getUserByName(actBusiness.getUserId());
                actZprocessService.sendMessage(actBusiness.getId(),loginUser,formUser,ActivitiConstant.MESSAGE_PASS_CONTENT,
                        String.format("您的 【%s】 投诉已受理！",actBusiness.getTitle()),sendMessage, sendSms, sendEmail);
            }

            //修改业务表的流程字段
            actBusinessService.updateBusinessStatus(actBusiness.getTableName(), actBusiness.getTableId(),"审批通过");

            //更新申诉表单状态
            actBusinessService.updateFormStatus(tableName,tableId,2);

        }
        // 记录实际审批人员
        actBusinessService.insertHI_IDENTITYLINK(IdUtil.simpleUUID(),
                ActivitiConstant.EXECUTOR_TYPE_p, loginUser.getUsername(), id, processInstanceId);
        return Result.ok("操作成功");
    }


    

    /*撤回申请*/
    @AutoLog(value = "流程-撤回申请")
    @ApiOperation(value="流程-撤回申请", notes="撤回申请")
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public Result<Object> cancel(@ApiParam(value = "流程扩展表id" ,required = true) @RequestParam String id,
                                 @ApiParam(value = "流程实例id" ,required = true) @RequestParam String procInstId,
                                 @ApiParam(value = "撤销理由原因说明" ,required = false) @RequestParam(required = false) String reason){

        if(StrUtil.isBlank(reason)){
            reason = "";
        }
        runtimeService.deleteProcessInstance(procInstId, "canceled-"+reason);
        ActBusiness actBusiness = actBusinessService.getById(id);
        actBusiness.setStatus(ActivitiConstant.STATUS_CANCELED);
        actBusiness.setResult(ActivitiConstant.RESULT_TO_SUBMIT);
        actBusinessService.updateById(actBusiness);
        //修改业务表的流程字段
        actBusinessService.updateBusinessStatus(actBusiness.getTableName(), actBusiness.getTableId(),"撤回");
        return Result.ok("操作成功");
    }
    /**/
    @AutoLog(value = "流程-流程列表")
    @ApiOperation(value="流程-流程列表", notes="流程列表，登录用户的流程列表")
    @RequestMapping(value = "/listData" ,method = RequestMethod.GET)
    public Result listData(ActBusiness param, HttpServletRequest request){
        LambdaQueryWrapper<ActBusiness> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(ActBusiness::getCreateTime);
        if (StrUtil.isNotBlank(param.getTitle())) queryWrapper.like(ActBusiness::getTitle,param.getTitle());
        if (param.getStatus()!=null) queryWrapper.eq(ActBusiness::getStatus,param.getStatus());
        //多个
        String statuss = request.getParameter("statuss");
        if (StrUtil.isNotBlank(statuss)) {
            queryWrapper.in(ActBusiness::getStatus,StrUtil.split(statuss,","));
        }
        if (param.getResult()!=null) queryWrapper.eq(ActBusiness::getResult,param.getResult());
        String createTime_begin = request.getParameter("createTime_begin");
        if (StrUtil.isNotBlank(createTime_begin))
            queryWrapper.ge(ActBusiness::getCreateTime,createTime_begin);
        String createTime_end = request.getParameter("createTime_end");
        if (StrUtil.isNotBlank(createTime_end))
            queryWrapper.le(ActBusiness::getCreateTime,createTime_end);

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //queryWrapper.eq(ActBusiness::getUserId,loginUser.getUsername());
        //流程类型
        String type = request.getParameter("type");
        if (StrUtil.isNotBlank(type)){
            List<String> actBusinessIdsByType = actBusinessService.listByTypeApp(type);
            if (actBusinessIdsByType.size()==0){ // 没有符合的 目的是上下面的查询条件也查不到
                queryWrapper.in(ActBusiness::getId, Lists.newArrayList(""));
            }else {
                queryWrapper.in(ActBusiness::getId,actBusinessIdsByType);
            }
        }
        List<ActBusiness> actBusinessList = actBusinessService.list(queryWrapper);

        // 是否需要业务数据
        String needData = request.getParameter("needData");
        actBusinessList.forEach(e -> {
            if(StrUtil.isNotBlank(e.getProcDefId())){
                ActZprocess actProcess = actZprocessService.getById(e.getProcDefId());
                e.setRouteName(actProcess.getRouteName());
                e.setProcessName(actProcess.getName());
            }
            if(StringUtils.isNotBlank(e.getApplyBy())&& loginUser.getUsername().equals(e.getApplyBy())){
                e.setCancelFlag(1);
            }else{
                e.setCancelFlag(0);
            }
            if(ActivitiConstant.STATUS_DEALING.equals(e.getStatus())){
                // 关联当前任务
                List<Task> taskList = taskService.createTaskQuery().processInstanceId(e.getProcInstId()).list();
                if(taskList!=null&&taskList.size()==1){
                    e.setCurrTaskName(taskList.get(0).getName());
                }else if(taskList!=null&&taskList.size()>1){
                    StringBuilder sb = new StringBuilder();
                    for(int i=0;i<taskList.size()-1;i++){
                        sb.append(taskList.get(i).getName()+"、");
                    }
                    sb.append(taskList.get(taskList.size()-1).getName());
                    e.setCurrTaskName(sb.toString());
                }
            }
            if (StrUtil.equals(needData,"true")){ // 需要业务数据
                Map<String, Object> applyForm = actBusinessService.getApplyForm(e.getTableId(), e.getTableName());
                e.setDataMap(applyForm);
            }
        });
        return Result.ok(actBusinessList);
    }


    @AutoLog(value = "流程-回访说明")
    @ApiOperation(value="流程-回访说明", notes="回访说明")
    @RequestMapping(value = "/addRemark", method = RequestMethod.POST)
    public Result<Object> addRemark(@ApiParam(value = "数据表ID" ,required = true) @RequestParam String tableId,
                                    @ApiParam(value = "数据表名称" ,required = true) @RequestParam String tableName,
                                    @ApiParam(value = "回访说明" ,required = false) @RequestParam(required = false) String reason,
                                    @ApiParam(value = "回访满意度" ,required = false) @RequestParam(required = false) String degree){
        if(StrUtil.isBlank(reason)){
            reason = "";
        }

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //修改业务表的流程字段
        if(StringUtils.isBlank(degree)){
            degree = "4";
        }
        ActBusiness actBusiness = actBusinessService.getOneBean(new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getTableId,tableId));
        actBusiness.setVisitFlag(1);
        actBusinessService.update(actBusiness,new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getTableId,tableId));

        ActBusinessVisit visit = new ActBusinessVisit();
        visit.setBusinessId(actBusiness.getId());
        visit.setVisitor(loginUser.getUsername());
        visit.setVisitRank(degree);
        visit.setVisitTime(new Date());
        visit.setVisitRemark(reason);
        visit.setProcInstId(actBusiness.getProcInstId());
        actBusinessService.saveBusinessVisit(visit);
        actBusinessService.updateFormStatus(tableName,tableId,3);
        return Result.ok("操作成功");
    }


    @AutoLog(value = "流程-获取回访列表")
    @ApiOperation(value="流程-获取回访列表", notes="流程-获取回访列表")
    @RequestMapping(value = "/getVisitList", method = RequestMethod.GET)
    public Result getVisitList(@ApiParam(value = "流程ID" ,required = true)@RequestParam("procInstId") String procInstId){
        return Result.ok(actBusinessService.getVisitList(procInstId));
    }
}

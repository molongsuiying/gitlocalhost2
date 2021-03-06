package org.jeecg.modules.activiti.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CommonSendStatus;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.modules.activiti.entity.*;
import org.jeecg.modules.activiti.mapper.ActZprocessMapper;
import org.jeecg.modules.activiti.service.IActZprocessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: ?????????????????????
 * @Author: pmc
 * @Date:   2020-03-22
 * @Version: V1.0
 */
@Service
public class ActZprocessServiceImpl extends ServiceImpl<ActZprocessMapper, ActZprocess> implements IActZprocessService {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;
    @Autowired
    private ActNodeServiceImpl actNodeService;
    @Autowired
    private ActBusinessServiceImpl actBusinessService;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    /**
     * ??????key????????????????????????
     * @param processKey
     */
    public void setAllOldByProcessKey(String processKey) {
        List<ActZprocess> list = this.list(new LambdaQueryWrapper<ActZprocess>().eq(ActZprocess::getProcessKey,processKey));
        if(list==null||list.size()==0){
            return;
        }
        list.forEach(item -> {
            item.setLatest(false);
        });
        this.updateBatchById(list);
    }

    /**
     * ???????????????????????????
     * @param processKey
     */
    public void setLatestByProcessKey(String processKey) {
        ActZprocess actProcess = this.findTopByProcessKeyOrderByVersionDesc(processKey);
        if(actProcess==null){
            return;
        }
        actProcess.setLatest(true);
        this.updateById(actProcess);
    }

    private ActZprocess findTopByProcessKeyOrderByVersionDesc(String processKey) {
        List<ActZprocess> list = this.list(new LambdaQueryWrapper<ActZprocess>().eq(ActZprocess::getProcessKey, processKey)
                .orderByDesc(ActZprocess::getVersion)
        );
        if (CollUtil.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    public String startProcess(ActBusiness actBusiness) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // ??????????????????
        identityService.setAuthenticatedUserId(loginUser.getUsername());
        // ???????????? ??????????????????id??????
        Map<String, Object> params = actBusiness.getParams();
        params.put("tableId", actBusiness.getTableId());
        ActBusiness act = actBusinessService.getById(actBusiness.getId());
        String tableName = act.getTableName();
        String tableId = act.getTableId();
        if (StrUtil.isBlank(tableId)||StrUtil.isBlank(tableName)){
            throw new JeecgBootException("????????????????????????");
        }
        /*??????????????????*/
        Map<String, Object> busiData = actBusinessService.getBusiData(tableId, tableName);
        for (String key : busiData.keySet()) {
            params.put(key,busiData.get(key));
        }
        ProcessInstance pi = runtimeService.startProcessInstanceById(actBusiness.getProcDefId(), actBusiness.getId(), params);
        // ????????????????????????
        runtimeService.setProcessInstanceName(pi.getId(), actBusiness.getTitle());
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(pi.getId()).list();
        for(Task task : tasks){
            if(actBusiness.getFirstGateway()){
                // ????????????
                List<LoginUser> users = getNode(task.getTaskDefinitionKey()).getUsers();
                // ?????????????????????????????????????????? ??????????????????
                if(users==null||users.size()==0){
                    throw new RuntimeException("???????????????????????????????????????????????????????????????");
                }else{
                    // ???????????????????????????????????????
                    for(LoginUser user : users){
                        taskService.addCandidateUser(task.getId(), user.getUsername());
                        // ???????????????
                        sendActMessage(loginUser,user,actBusiness,task.getName(), actBusiness.getSendMessage(),
                                actBusiness.getSendSms(), actBusiness.getSendEmail());
                    }
                }
            }else {
                // ???????????????????????????
                String assignees = actBusiness.getAssignees();
                for (String assignee : assignees.split(",")) {
                    taskService.addCandidateUser(task.getId(), assignee);
                    // ???????????????
                    LoginUser user = sysBaseAPI.getUserByName(assignee);
                    sendActMessage(loginUser,user,actBusiness,task.getName(), actBusiness.getSendMessage(),
                            actBusiness.getSendSms(), actBusiness.getSendEmail());
                }
            }
            // ?????????????????????
            taskService.setPriority(task.getId(), actBusiness.getPriority());

            // ???????????????????????????
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(task.getCreateTime());
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + ActivitiConstant.PROCESS_DUE_DAY);
            Date dueTime = calendar.getTime();
            taskService.setDueDate(task.getId(),dueTime);
        }
        return pi.getId();
    }

    /**
     * ??????????????????
     * @param fromUser ?????????
     * @param toUser ?????????
     * @param act ??????
     * @param taskName
     * @param sendMessage ????????????
     * @param sendSms ????????????
     * @param sendEmail ????????????
     */
    public void sendActMessage(LoginUser fromUser, LoginUser toUser, ActBusiness act, String taskName, Boolean sendMessage, Boolean sendSms, Boolean sendEmail) {
        String title = String.format("??????????????????????????????");
        Map<String, String> msgMap = Maps.newHashMap();
                        /*???????????????  ${bpm_name}
        ???????????????  ${bpm_task}
        ???????????? :    ${datetime}
        ???????????? :    ${remark}*/
        msgMap.put("bpm_name",act.getTitle());
        msgMap.put("bpm_task",taskName);
        msgMap.put("datetime", DateUtils.now());
        msgMap.put("remark", "????????????????????????????????????");
        /*??????????????????*/
        String msgText = sysBaseAPI.parseTemplateByCode("bpm_cuiban", msgMap);
        this.sendMessage(act.getId(),fromUser,toUser,title,msgText,sendMessage,sendSms,sendEmail);
    }

    /**
     * ??????????????????
     * @param fromUser ?????????
     * @param toUser ?????????
     * @param act ??????
     * @param taskName
     * @param sendMessage ????????????
     * @param sendSms ????????????
     * @param sendEmail ????????????
     */
    public void sendUrgentMessage(LoginUser fromUser, LoginUser toUser, ActBusiness act, String taskName,String createTime ,String remark, Boolean sendMessage, Boolean sendSms, Boolean sendEmail) {
        String title = String.format("??????????????????????????????");
        Map<String, String> msgMap = Maps.newHashMap();
        /*?????????????????????  ${title}
        ?????????????????????  ${bpm_task}
        ?????????????????? :    ${datetime}
         ????????? :    ${user}
        ???????????? :    ${remark}*/
        msgMap.put("title",act.getTitle());
        msgMap.put("task",taskName);
        msgMap.put("time", createTime);
        msgMap.put("user",fromUser.getRealname());
        msgMap.put("remark", remark);
        /*??????????????????*/
        String msgText = sysBaseAPI.parseTemplateByCode(CommonSendStatus.TZMB_BPM_CHAOSHI_TIP, msgMap);
        this.sendMessage(act.getId(),fromUser,toUser,title,msgText,sendMessage,sendSms,sendEmail);
    }

    /**
     * ??????????????????
     * @param fromUser ?????????
     * @param toUser ?????????
     * @param act ??????
     * @param taskName
     * @param sendMessage ????????????
     * @param sendSms ????????????
     * @param sendEmail ????????????
     */
    public void sendSuperviseMessage(LoginUser fromUser, LoginUser toUser, ActBusiness act, String taskName,String createTime ,String remark, Boolean sendMessage, Boolean sendSms, Boolean sendEmail) {
        String title = String.format("??????????????????????????????");
        Map<String, String> msgMap = Maps.newHashMap();
        /*?????????????????????  ${title}
        ?????????????????????  ${bpm_task}
        ?????????????????? :    ${datetime}
         ????????? :    ${user}
        ???????????? :    ${remark}*/
        msgMap.put("title",act.getTitle());
        msgMap.put("task",taskName);
        msgMap.put("time", createTime);
        msgMap.put("user",fromUser.getRealname());
        msgMap.put("remark", remark);
        /*??????????????????*/
        String msgText = sysBaseAPI.parseTemplateByCode(CommonSendStatus.TZMB_BPM_CHAOSHI_TIP, msgMap);
        this.sendMessage(act.getId(),fromUser,toUser,title,msgText,sendMessage,sendSms,sendEmail);
    }

    /**
     * ??????????????????
     * @param fromUser ?????????
     * @param toUser ?????????
     * @param act ??????
     * @param taskName
     * @param sendMessage ????????????
     * @param sendSms ????????????
     * @param sendEmail ????????????
     */
    public void sendDueMessage(String title, LoginUser fromUser, LoginUser toUser, ActBusiness act, String taskName,String createTime ,String remark, Boolean sendMessage, Boolean sendSms, Boolean sendEmail) {

        Map<String, String> msgMap = Maps.newHashMap();
        /*?????????????????????  ${title}
        ?????????????????????  ${bpm_task}
        ?????????????????? :    ${datetime}
         ????????? :    ${user}
        ???????????? :    ${remark}*/
        msgMap.put("title",act.getTitle());
        msgMap.put("task",taskName);
        msgMap.put("time", createTime);
        msgMap.put("user",fromUser.getRealname());
        msgMap.put("remark", remark);
        /*??????????????????*/
        String msgText = sysBaseAPI.parseTemplateByCode(CommonSendStatus.TZMB_BPM_CHAOSHI_TIP, msgMap);
        this.sendMessage(act.getId(),fromUser,toUser,title,msgText,sendMessage,sendSms,sendEmail);
    }

    /**
     * ?????????
     * @param actBusId ????????????id
     * @param fromUser ?????????
     * @param toUser ?????????
     * @param title ??????
     * @param msgText ????????????
     * @param sendMessage ????????????
     * @param sendSms ??????
     * @param sendEmail ??????
     */
    public void sendMessage(String actBusId,LoginUser fromUser, LoginUser toUser,String title,String msgText,  Boolean sendMessage, Boolean sendSms, Boolean sendEmail) {
        if (sendMessage!=null&&sendMessage){
            sysBaseAPI.sendSysAnnouncement_act(actBusId,fromUser.getUsername(),toUser.getUsername(),title,msgText);
        }
        //todo ???????????????????????????????????????????????????????????????
        if (sendSms!=null&&sendSms&& StrUtil.isNotBlank(toUser.getPhone())){
            //DySmsHelper.sendSms(toUser.getPhone(), obj, DySmsEnum.REGISTER_TEMPLATE_CODE)
        }
        if (sendEmail!=null&&sendEmail&& StrUtil.isNotBlank(toUser.getEmail())){
            JavaMailSender mailSender = (JavaMailSender) SpringContextUtils.getBean("mailSender");
            SimpleMailMessage message = new SimpleMailMessage();
            // ???????????????????????????
            // message.setFrom(emailFrom);
            message.setTo(toUser.getEmail());
            //message.setSubject(es_title);
            message.setText(msgText);
            mailSender.send(message);
        }
    }

    public ProcessNodeVo getNode(String nodeId) {

        ProcessNodeVo node = new ProcessNodeVo();
        // ??????????????????
        List<LoginUser> users = getNodetUsers(nodeId);
        node.setUsers(removeDuplicate(users));
        return node;
    }
    /**
     * ?????????????????????
     * @param nodeId
     */
    public List<LoginUser> getNodetUsers(String nodeId){
        List<LoginUser> users = actNodeService.findUserByNodeId(nodeId);
        // ??????????????????????????? 0
        List<Role> roles = actNodeService.findRoleByNodeId(nodeId);
        for(Role r : roles){
            List<LoginUser> userList = actNodeService.findUserByRoleId(r.getId());
            users.addAll(userList);
        }
        // ???????????????????????????
        List<Department> departments = actNodeService.findDepartmentByNodeId(nodeId);
        for (Department d : departments){
            List<LoginUser> userList = actNodeService.findUserDepartmentId(d.getId());
            users.addAll(userList);
        }
        // ???????????????????????????
        if(actNodeService.hasChooseDepHeader(nodeId)){
            List<LoginUser> allUser = actNodeService.queryAllUser();
            //???????????????????????????
            String createBy = getUserByNodeId(nodeId);
            List<String> departIds = sysBaseAPI.getDepartIdsByUsername(createBy);

            for (String departId : departIds) {
                List<LoginUser> collect = allUser.stream().filter(u -> u.getDepartIds() != null && u.getDepartIds().contains(departId)).collect(Collectors.toList());
                users.addAll(collect);
            }
        }
        //??????????????????????????????????????????
        List<Role> appointRoles = actNodeService.findAppointRoleByNodeId(nodeId);

        //???????????????
        String username = getUserByNodeId(nodeId);
        List<String> departIds = sysBaseAPI.getDepartIdsByUsername(username);

        //??????????????????????????????????????????????????????????????? 5
        if(departIds.size()>0){
            String departId = departIds.get(0);
            //??????????????????
            DictModel parentDict = sysBaseAPI.getParentDepartId(departId);
            if(parentDict != null){
                //????????????????????????????????????
                List<String> subDepIds = sysBaseAPI.getSubDepIdsByDepId(parentDict.getText());
                List<String> appointRoleIds = appointRoles.stream().map(Role::getId).collect
                        (Collectors.toList());
                if(appointRoleIds.size()>0 && subDepIds.size()>0){
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("roleIds",appointRoleIds);
                    map.put("departIds",subDepIds);
                    List<LoginUser> userList = actNodeService.findUserByRoleIdsAndDepartIds(map);
                    users.addAll(userList);
                }
            }

        }
        //???????????????????????????????????? 6
        List<Department> appointDepartments = actNodeService.findAppointDepartmentByNodeId(nodeId);
        if(appointDepartments.size()>0){
            String parentDepartId = appointDepartments.get(0).getId();
            List<String> subDepIds = sysBaseAPI.getSubDepIdsByDepId(parentDepartId);
            HashMap<String,Object> map = new HashMap<>();
            map.put("departIds",subDepIds);
            List<LoginUser> userList = actNodeService.findUserByRoleIdsAndDepartIds(map);
            users.addAll(userList);
        }

        //???????????????
        String custom = actNodeService.findRelateIdByNodeId(nodeId);

        if(StringUtils.isNotBlank(custom)){
            JSONObject object = JSONObject.parseObject(custom);
            HashMap<String,Object> c_map = new HashMap<>();
            String c_roleCodes = object.get("roleCodes").toString();
            String c_departIds = object.get("departIds").toString();
            boolean autoDepartFlag = false;
            if(object.containsKey("autoDepartFlag")){
                autoDepartFlag = (boolean) object.get("autoDepartFlag");
            }
            if(StringUtils.isNotBlank(c_roleCodes)){

                String str[] = c_roleCodes.split(",");
                List<String> roleList = Arrays.asList(str);
                c_map.put("roleIds",roleList);
            }
            if(StringUtils.isNotBlank(c_departIds)){

                String str2[] = c_departIds.split(",");
                List<String> ids = Arrays.asList(str2);
                if(ids.size()>0){
                    c_map.put("departIds",ids);
                }
            }else{
                if(autoDepartFlag){
                    LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    SysDepartModel departModel = sysBaseAPI.getDepartByCode(loginUser.getOrgCode());
                    //????????????????????????????????????
                    List<String> ids = sysBaseAPI.getSubDepIdsByDepId(departModel.getId());
                    // ??????????????????
                    c_map.put("departIds",ids);
                }
            }
            List<LoginUser> userList = actNodeService.findUserByRoleIdsAndDepartIds(c_map);
            users.addAll(userList);
        }


        // ????????????????????? 3
        if(actNodeService.hasChooseSponsor(nodeId)){
            String createBy = getUserByNodeId(nodeId);
            LoginUser userByName = sysBaseAPI.getUserByName(createBy);
            users.add(userByName);
        }
        users = users.stream().filter(u->StrUtil.equals("0",u.getDelFlag()+"")).collect(Collectors.toList());
        return users;
    }


    /**
     * ????????????id???????????????
     * @param nodeId
     * @return
     */
    public String getUserByNodeId(String nodeId) {
        LambdaQueryWrapper<ActNode> actNodeList = new LambdaQueryWrapper<ActNode>().eq(ActNode::getNodeId, nodeId);

        ActNode actNode = actNodeService.getOneBean(actNodeList);
        //ActNode actNode = actNodeService.getById(nodeId);
        String procDefId = actNode.getProcDefId();
        ActBusiness actBusiness = actBusinessService.getOneBean(new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getProcDefId, procDefId));
        return actBusiness.getCreateBy();
    }

    /**
     * ??????
     * @param list
     * @return
     */
    private List<LoginUser> removeDuplicate(List<LoginUser> list) {

        LinkedHashSet<LoginUser> set = new LinkedHashSet<>(list.size());
        set.addAll(list);
        list.clear();
        list.addAll(set);
        entityManager.clear();
        list.forEach(u -> {
            u.setPassword(null);
        });
        return list;
    }

    public ProcessNodeVo getFirstNode(String procDefId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);

        ProcessNodeVo node = new ProcessNodeVo();

        List<Process> processes = bpmnModel.getProcesses();
        Collection<FlowElement> elements = processes.get(0).getFlowElements();
        // ??????????????????
        StartEvent startEvent = null;
        for (FlowElement element : elements) {
            if (element instanceof StartEvent) {
                startEvent = (StartEvent) element;
                break;
            }
        }
        FlowElement e = null;
        // ??????????????????????????????
        SequenceFlow sequenceFlow = startEvent.getOutgoingFlows().get(0);
        for (FlowElement element : elements) {
            if(element.getId().equals(sequenceFlow.getTargetRef())){
                if(element instanceof UserTask){
                    e = element;
                    node.setType(1);
                    break;
                }else if(element instanceof ExclusiveGateway){
                    e = element;
                    node.setType(3);
                    break;
                }else if(element instanceof ParallelGateway){
                    e = element;
                    node.setType(4);
                    break;
                }else{
                    throw new RuntimeException("?????????????????????????????????????????????????????????????????????????????????????????????");
                }
            }
        }
        // ?????????????????????????????????
        if(e instanceof ExclusiveGateway || e instanceof ParallelGateway){
            return node;
        }
        node.setTitle(e.getName());

        //??????????????????????????????
        List<ActNode> actNodeList = actNodeService.findByNodeIdAndType(e.getId(),7);

        if(actNodeList!=null&&actNodeList.size()>0){
            ActNode actNode = actNodeList.get(0);
            node.setChooseCustomized(true);
            String relateId = actNode.getRelateId();

            JSONObject object = JSONObject.parseObject(relateId);

            String roleCodes = object.get("roleCodes").toString();
            System.out.println(roleCodes+"==========");
            String departIds = object.get("departIds").toString();
            boolean autoDepartFlag = false;
            if(object.containsKey("autoDepartFlag")){
                autoDepartFlag = (boolean) object.get("autoDepartFlag");
            }

            if(StringUtils.isNotBlank(roleCodes)){

                String str[] = roleCodes.split(",");
                List<String> roleList = Arrays.asList(str);
                // ??????????????????
                node.setRoles(actNodeService.findRolesByCodes(roleList));
            }

            if(StringUtils.isNotBlank(departIds)){
                String str[] = departIds.split(",");
                List<String> ids = Arrays.asList(str);
                // ??????????????????
                node.setDepartments(actNodeService.findDepartmentsByIds(ids));
            }else {
                if(autoDepartFlag){
                    LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    SysDepartModel departModel = sysBaseAPI.getDepartByCode(loginUser.getOrgCode());
                    List<String> ids = new ArrayList<>();
                    ids.add(departModel.getId());
                    // ??????????????????
                    node.setDepartments(actNodeService.findDepartmentsByIds(ids));
                }
                node.setAutoDepartFlag(autoDepartFlag);
            }
        }else{
            // ??????????????????
            List<LoginUser> users = getNodetUsers(e.getId());
            node.setUsers(removeDuplicate(users));
        }

        return node;
    }

    public ProcessNodeVo getNextNode(String procDefId, String currActId) {
        ProcessNodeVo node = new ProcessNodeVo();
        // ??????????????????id
        ProcessDefinitionEntity dfe = (ProcessDefinitionEntity) ((RepositoryServiceImpl)repositoryService).getDeployedProcessDefinition(procDefId);
        // ??????????????????
        List<ActivityImpl> activitiList = dfe.getActivities();
        // ???????????????????????????????????????????????????????????????????????????
        for(ActivityImpl activityImpl : activitiList){
            if (activityImpl.getId().equals(currActId)) {
                // ?????????????????????
                List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();

                PvmActivity pvmActivity = pvmTransitions.get(0).getDestination();

                String type = pvmActivity.getProperty("type").toString();
                if("userTask".equals(type)){
                    // ??????????????????
                    node.setType(ActivitiConstant.NODE_TYPE_TASK);
                    node.setTitle(pvmActivity.getProperty("name").toString());


                    //??????????????????????????????
                    List<ActNode> actNodeList = actNodeService.findByNodeIdAndType(pvmActivity.getId(),7);

                    if(actNodeList!=null&&actNodeList.size()>0){
                        ActNode actNode = actNodeList.get(0);
                        node.setChooseCustomized(true);
                        String relateId = actNode.getRelateId();

                        JSONObject object = JSONObject.parseObject(relateId);

                        String roleCodes = object.get("roleCodes").toString();
                        String departIds = object.get("departIds").toString();

                        boolean autoDepartFlag = false;
                        if(object.containsKey("autoDepartFlag")){
                            autoDepartFlag = (boolean) object.get("autoDepartFlag");
                        }
                        if(StringUtils.isNotBlank(roleCodes)){

                            String str[] = roleCodes.split(",");
                            List<String> roleList = Arrays.asList(str);
                            // ??????????????????
                            node.setRoles(actNodeService.findRolesByCodes(roleList));
                        }

                        if(StringUtils.isNotBlank(departIds)){
                            String str[] = departIds.split(",");
                            List<String> ids = Arrays.asList(str);
                            // ??????????????????
                            node.setDepartments(actNodeService.findDepartmentsByIds(ids));
                        }else{
                            if (autoDepartFlag){
                                LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                                SysDepartModel departModel = sysBaseAPI.getDepartByCode(loginUser.getOrgCode());
                                List<String> ids = new ArrayList<>();
                                ids.add(departModel.getId());
                                // ??????????????????
                                node.setDepartments(actNodeService.findDepartmentsByIds(ids));
                            }

                            node.setAutoDepartFlag(autoDepartFlag);
                        }
                    }else{
                        // ??????????????????
                        List<LoginUser> users = getNodetUsers(pvmActivity.getId());
                        node.setUsers(removeDuplicate(users));
                    }


                }else if("exclusiveGateway".equals(type)){
                    // ????????????
                    node.setType(ActivitiConstant.NODE_TYPE_EG);
                    ActivityImpl pvmActivity1 = (ActivityImpl) pvmActivity;
                    /*????????????Id*/
                    String procInsId = "";
                    /*????????????*/
                    Map<String, Object> vals = Maps.newHashMap();
                    List<ActBusiness> actBbyProcDefId = actBusinessService.findByProcDefId(procDefId);
                    if (CollUtil.isNotEmpty(actBbyProcDefId)){
                        ActBusiness actBusiness = actBbyProcDefId.get(0);
                        vals = actBusinessService.getApplyForm(actBusiness.getTableId(), actBusiness.getTableName());
                        procInsId = actBusiness.getProcInstId();
                    }
                    TaskDefinition taskDefinition = actNodeService.nextTaskDefinition(pvmActivity1, pvmActivity1.getId(), vals, procInsId);
                    List<LoginUser> users = getNodetUsers(taskDefinition.getKey());
                    node.setUsers(removeDuplicate(users));
                }else if("parallelGateway".equals(type)){
                    // ????????????
                    node.setType(ActivitiConstant.NODE_TYPE_PG);
                }else if("endEvent".equals(type)){
                    // ??????
                    node.setType(ActivitiConstant.NODE_TYPE_END);
                }else{
                    throw new JeecgBootException("????????????????????????????????????????????????");
                }
                break;
            }
        }

        return node;
    }


    @Override
    public List<ActZprocess> selectZprocessList(Map<String, Object> map) {
        return this.baseMapper.selectZprocessList(map);
    }

    @Override
    public int countZprocess(Map<String, Object> map) {
        return this.baseMapper.countZprocess(map);
    }
}

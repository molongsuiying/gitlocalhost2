package org.jeecg.modules.activiti.service.Impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.ComboModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.activiti.data.*;
import org.jeecg.modules.activiti.entity.*;
import org.jeecg.modules.activiti.mapper.ActBusinessMapper;
import org.jeecg.modules.activiti.service.IActBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 流程业务扩展表
 * @Author: pmc
 * @Date:   2020-03-30
 * @Version: V1.0
 */
@Service
public class ActBusinessServiceImpl extends ServiceImpl<ActBusinessMapper, ActBusiness> implements IActBusinessService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ActZprocessServiceImpl actZprocessService;

    @Autowired
    private ActNodeServiceImpl actNodeService;

    @Autowired
    ISysBaseAPI sysBaseAPI;

    @Autowired
    private HistoryService historyService;

    public List<ActBusiness> findByProcDefId(String id) {
       return this.list(new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getProcDefId,id));
    }
    /**保存业务表单数据到数据库表
     * <br>该方法相对通用，复杂业务单独定制，套路类似
     * @param tableId 业务表中的数据id
     * @return  如果之前数据库没有 返回 true
     * */
    public boolean saveApplyForm(String tableId, HttpServletRequest request) {
        String tableName = request.getParameter("tableName");
        String filedNames = request.getParameter("filedNames");

        Map<String, Object> busiData = this.baseMapper.getBusiData(tableId, tableName);
        String[] fileds = filedNames.split(",");
        if (MapUtil.isEmpty(busiData)){ //没有，新增逻辑
            StringBuilder filedsB = new StringBuilder("id");
            StringBuilder filedsVB = new StringBuilder("'"+tableId+"'");
            for (String filed : fileds) {
                String dbFiled = oConvertUtils.camelToUnderline(filed);
                if(filed != null && !filed.equals("undefined")){
                    if(request.getParameter(filed) != null){
                        filedsB.append(","+dbFiled);

                        if(dbFiled.contains("begin_time")){
                            filedsVB.append(",'"+request.getParameter(filed).replace("\"", "")+"'");
                        }else{
                            filedsVB.append(",'"+request.getParameter(filed)+"'");
                        }


                    }else{
                        filedsB.append(","+dbFiled);
                        filedsVB.append(","+request.getParameter(filed));
                    }
                }
            }
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            String userName = sysUser.getUsername();
            filedsB.append(","+"create_by");
            filedsVB.append(",'"+userName+"'");
            filedsB.append(","+"create_time");
            filedsVB.append(",'"+ DateUtils.formatDate(new Date(),"yyyy-MM-dd") +"'");
            this.baseMapper.insertBusiData(String.format("INSERT INTO %s (%s) VALUES (%s)",tableName,filedsB.toString(),filedsVB.toString()));
        }else { //有，修改
            StringBuilder setSql = new StringBuilder();
            for (String filed : fileds) {
                if(filed != null && !filed.equals("undefined")){
                    String parameter = request.getParameter(filed);
                    String dbFiled = oConvertUtils.camelToUnderline(filed);
                    if (parameter==null){
                        setSql.append(String.format("%s = null,",dbFiled));
                    }else {
                        setSql.append(String.format("%s = '%s',",dbFiled, parameter));
                    }
                }
            }
            String substring = setSql.substring(0, setSql.length()-1);//去掉最后一个,号
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            String userName = sysUser.getUsername();
            substring += (",update_by = " +    "'"+userName+"'");
            substring += (",update_time = " + "'" +DateUtils.formatDate(new Date(),"yyyy-MM-dd")+"'");
            this.baseMapper.updateBusiData(String.format("update %s set %s where id = '%s'",tableName,substring,tableId));
        }
        return MapUtil.isEmpty(busiData);
    }

    /**保存业务表单数据到数据库表
     * <br>该方法相对通用，复杂业务单独定制，套路类似
     * @param tableId 业务表中的数据id
     * @return  如果之前数据库没有 返回 true
     * */
    public boolean saveApplyFormByOut(String tableId, JSONObject jsonObject) {
        String tableName = jsonObject.getString("tableName");
        String filedNames = jsonObject.getString("filedNames");

        Map<String, Object> busiData = this.baseMapper.getBusiData(tableId, tableName);
        String[] fileds = filedNames.split(",");
        if (MapUtil.isEmpty(busiData)){ //没有，新增逻辑
            StringBuilder filedsB = new StringBuilder("id");
            StringBuilder filedsVB = new StringBuilder("'"+tableId+"'");
            for (String filed : fileds) {
                String dbFiled = oConvertUtils.camelToUnderline(filed);
                if(filed != null && !filed.equals("undefined")){
                    if(jsonObject.getString(filed) != null){
                        filedsB.append(","+dbFiled);

                        if(dbFiled.contains("begin_time")){
                            filedsVB.append(",'"+jsonObject.getString(filed).replace("\"", "")+"'");
                        }else{
                            filedsVB.append(",'"+jsonObject.getString(filed)+"'");
                        }


                    }else{
                        filedsB.append(","+dbFiled);
                        filedsVB.append(","+jsonObject.getString(filed));
                    }
                }
            }
            //LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            //String userName = sysUser.getUsername();
            filedsB.append(","+"create_by");
            filedsVB.append(",''");
            filedsB.append(","+"create_time");
            filedsVB.append(",'"+ DateUtils.formatDate(new Date(),"yyyy-MM-dd") +"'");
            this.baseMapper.insertBusiData(String.format("INSERT INTO %s (%s) VALUES (%s)",tableName,filedsB.toString(),filedsVB.toString()));
        }else { //有，修改
            StringBuilder setSql = new StringBuilder();
            for (String filed : fileds) {
                if(filed != null && !filed.equals("undefined")){
                    String parameter = jsonObject.getString(filed);
                    String dbFiled = oConvertUtils.camelToUnderline(filed);
                    if (parameter==null){
                        setSql.append(String.format("%s = null,",dbFiled));
                    }else {
                        setSql.append(String.format("%s = '%s',",dbFiled, parameter));
                    }
                }
            }
            String substring = setSql.substring(0, setSql.length()-1);//去掉最后一个,号
            substring += (",update_by = " +    "''");
            substring += (",update_time = " + "'" +DateUtils.formatDate(new Date(),"yyyy-MM-dd")+"'");
            this.baseMapper.updateBusiData(String.format("update %s set %s where id = '%s'",tableName,substring,tableId));
        }
        return MapUtil.isEmpty(busiData);
    }

    /**
     * 根据部门id获取审批人
     * @param departIds
     * @return
     */
    public String getAssigneesByDepartId(String departIds){
        List<String> subDepIds = sysBaseAPI.getSubDepIdsByDepId(departIds);
        HashMap<String,Object> map = new HashMap<>();
        map.put("departIds",subDepIds);
        List<LoginUser> userList = actNodeService.findUserByRoleIdsAndDepartIds(map);
        List<String> nameList = userList.stream().map(LoginUser::getUsername).collect(Collectors.toList());
        String assignees = StringUtils.join(nameList.toArray(), ",");
        return assignees;
    }

    public Map<String, Object> getApplyForm(String tableId, String tableName) {
        Map<String, Object> busiData = this.getBusiData(tableId, tableName);
        boolean flag = busiData.containsKey("createBy");
        if(flag){
            Object createBy = busiData.get("createBy");
            if (createBy != null && StringUtils.isNotBlank(createBy.toString())){
                String depName = sysBaseAPI.getDepartNamesByUsername(createBy.toString()).get(0);
                busiData.put("createByDept",depName);
                LoginUser userByName = sysBaseAPI.getUserByName(createBy.toString());
                if(userByName != null){
                    busiData.put("createByName",userByName.getRealname());
                    busiData.put("createByAvatar",userByName.getAvatar());
                }
            }
        }

        return busiData;
    }


    public List<Map<String, Object>> getApplyFormList(String tableName ,String categoryId, String status,String name,String mobile, String pageNo, String pageSize) {
        List<Map<String, Object>> busiDataList = this.getBusiDataList(tableName, categoryId, status,name,mobile, pageNo, pageSize);

        return busiDataList;
    }

    public int countApplyFormList(String tableName ,String categoryId, String status,String name,String mobile) {
        return this.baseMapper.countBusiDataList(tableName, categoryId, status, name,mobile);
    }

    public void updateFormStatus(String tableName, String tableId,int status){
        this.baseMapper.updateFormStatus(tableName,tableId,status);
    }


    public void deleteBusiness(String tableName, String tableId) {
        this.baseMapper.deleteBusiData(tableId,tableName);
    }
    /**
     *通过类型和任务id查找用户id
     * */
    public List<String> findUserIdByTypeAndTaskId(String type, String taskId) {
        return this.baseMapper.findUserIdByTypeAndTaskId(type, taskId);
    }
    /**
     *通过类型和任务id查找用户idList
     * */
    public List<String> findUserIdListByTypeAndTaskId(String type, String taskId) {
        return this.baseMapper.findUserIdListByTypeAndTaskId(type, taskId);
    }


    public void insertHI_IDENTITYLINK(String id, String type, String userId, String taskId, String procInstId) {
        this.baseMapper.insertHI_IDENTITYLINK(id, type, userId, taskId, procInstId);
    }

    public void insertHI_TASKINST(String id, String procDefId, String taskDefKey,
                                    String procInstId, String exId, String name,
                                    Date startTime, Date endTime, String reason){
       this.baseMapper.insertHI_TASKINST(id, procDefId, taskDefKey, procInstId, exId, name, startTime, endTime, reason);

    }

    public  void insertHI_COMMENT(String id,String type, Date time,String userId, String taskId, String procInstId, String action, String message){
        this.baseMapper.insertHI_COMMENT(id, type, time, userId, taskId, procInstId, action, message,message.getBytes());
    }

    public List<String> selectIRunIdentity(String taskId, String type) {
       return this.baseMapper.selectIRunIdentity(taskId,type);
    }
    /**修改业务表的流程字段*/
    public void updateBusinessStatus(String tableName, String tableId, String actStatus) {
        try {
            this.baseMapper.updateBusinessStatus(tableName,tableId,actStatus);
        } catch (Exception e) {
             // 业务表需要有 act_status字段，没有会报错，不管他
            //e.printStackTrace();
            log.warn(e.getMessage());
        }
    }

    /**
     * 获取业务表单数据并驼峰转换
     * */
    public Map<String, Object> getBusiData(String tableId, String tableName) {
        Map<String, Object> busiData = this.baseMapper.getBusiData(tableId, tableName);
        if (busiData==null) return null;
        HashMap<String, Object> map = Maps.newHashMap();
        for (String key : busiData.keySet()) {
            String camelName = oConvertUtils.camelName(key);
            map.put(camelName,busiData.get(key));
        }
        return map;
    }

    /**
     * 获取业务表单数据并驼峰转换
     * */
    public List<Map<String, Object>> getBusiDataList(String tableName ,String categoryId, String status,String name,String mobile,String pageNo, String pageSize) {

        List<Map<String, Object>> busiDataList = this.baseMapper.getBusiDataList(tableName, categoryId,status, name, mobile, pageNo, pageSize);

        if (busiDataList.size()==0) return null;

        List<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < busiDataList.size(); i++) {
            Map<String, Object> busiData = busiDataList.get(i);
            Map<String, Object> map = Maps.newHashMap();
            for (String key : busiData.keySet()) {
                String camelName = oConvertUtils.camelName(key);
                map.put(camelName,busiData.get(key));
            }
            mapList.add(map);
        }

        return mapList;
    }



    public List<String> listByTypeApp(String type) {
        return this.baseMapper.listByTypeApp(type);
    }



    /**
     *  获取登陆人的已办
     *
     * @param req
     * @param name 流程名
     * @param categoryId 流程类型
     * @param priority 优先级别
     * @return
     */

    public List<HistoricTaskVo> getHistoricTaskVos(HttpServletRequest req, String name, String categoryId, Integer priority) {
        List<HistoricTaskVo> list = new ArrayList<>();
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getUsername();
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().or().taskCandidateUser(userId).
                taskAssignee(userId).endOr().finished();

        // 多条件搜索
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
        String searchVal = req.getParameter("searchVal");
        if (StrUtil.isNotBlank(searchVal)){
            //搜索标题、申请人
            List<LoginUser> usersByName = sysBaseAPI.getUsersByName(searchVal);
            List<String> uNames = null;
            if (usersByName.size()==0){
                uNames = Lists.newArrayList("");
            }else {
                uNames = usersByName.stream().map(u->u.getUsername()).collect(Collectors.toList());
            }
            List<ActBusiness> businessList = this.list(new LambdaQueryWrapper<ActBusiness>()
                    .like(ActBusiness::getTitle, searchVal) //标题查询
                    .or().in(ActBusiness::getUserId,uNames)
            );
            if (businessList.size()>0){
                // 定义id
                List<String> pids = businessList.stream().filter(act -> act.getProcInstId()!=null).map(act -> act.getProcInstId()).collect(Collectors.toList());
                query.processInstanceIdIn(pids);
            }else {
                query.processInstanceIdIn(Lists.newArrayList(""));
            }
        }
        String type = req.getParameter("type");
        if (StrUtil.isNotBlank(type)){
            List<String> deployment_idList = this.getBaseMapper().deployment_idListByType(type);
            if (deployment_idList.size()==0){
                query.deploymentIdIn(Lists.newArrayList(""));
            }else {
                query.deploymentIdIn(deployment_idList);
            }
        }
        String createTime_end = req.getParameter("createTime_end");
        if(StrUtil.isNotBlank(createTime_end)){
            Date end = DateUtil.parse(createTime_end);
            query.taskCreatedBefore(DateUtil.endOfDay(end));
        }
        List<HistoricTaskInstance> taskList = query.list();
        // 是否需要业务数据
        String needData = req.getParameter("needData");
        // 转换vo
        List<ComboModel> allUser = sysBaseAPI.queryAllUser();
        Map<String, String> userMap = allUser.stream().collect(Collectors.toMap(ComboModel::getUsername, ComboModel::getTitle));
        taskList.forEach(e -> {
            HistoricTaskVo htv = new HistoricTaskVo(e);
            // 关联委托人
            if(StrUtil.isNotBlank(htv.getOwner())){
                htv.setOwner(userMap.get(htv.getOwner()));
            }
            List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForProcessInstance(htv.getProcInstId());
            for(HistoricIdentityLink hik : identityLinks){
                // 关联发起人
                if("starter".equals(hik.getType())&&StrUtil.isNotBlank(hik.getUserId())){
                    htv.setApplyer(userMap.get(hik.getUserId()));
                }
            }
            // 关联审批意见
            List<Comment> comments = taskService.getTaskComments(htv.getId(), "comment");
            if(comments!=null&&comments.size()>0){
                htv.setComment(comments.get(0).getFullMessage());
            }
            // 关联流程信息
            ActZprocess actProcess = actZprocessService.getById(htv.getProcDefId());
            if(actProcess!=null){
                htv.setProcessName(actProcess.getName());
                htv.setRouteName(actProcess.getRouteName());
            }
            // 关联业务key
            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery().processInstanceId(htv.getProcInstId()).singleResult();
            htv.setBusinessKey(hpi.getBusinessKey());
            ActBusiness actBusiness = this.getById(hpi.getBusinessKey());
            if(actBusiness!=null){
                htv.setTableId(actBusiness.getTableId());
                htv.setTableName(actBusiness.getTableName());
                htv.setTitle(actBusiness.getTitle());
                htv.setStatus(actBusiness.getStatus());
                htv.setResult(actBusiness.getResult());
                if (StrUtil.equals(needData,"true")){ // 需要业务数据
                    Map<String, Object> applyForm = this.getApplyForm(actBusiness.getTableId(), actBusiness.getTableName());
                    htv.setDataMap(applyForm);
                }
            }

            list.add(htv);
        });
        return list;
    }

    /**
     * 新增首次额外扩展数据表
     * @param actBusiness
     */
    public void saveFirstBusinessExtra(ActBusiness actBusiness,boolean isSpeed){
        String[] assignees = actBusiness.getAssignees().split(",");

        List<LoginUser> userList = sysBaseAPI.queryUserByNames(assignees);

        //查找Task
        Task task = taskService.createTaskQuery().processInstanceId(actBusiness.getProcInstId()).processDefinitionId(actBusiness.getProcDefId()).singleResult();
        Map<String, List<LoginUser>> detailsMap = userList.stream()
                .collect(Collectors.groupingBy(LoginUser::getOrgCode));
        for (Map.Entry<String, List<LoginUser>> entry : detailsMap.entrySet()) {

            String code = entry.getKey();

            List<LoginUser> users = entry.getValue();

            List<String> names = users.stream().map(LoginUser::getUsername).collect(Collectors.toList());
            LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
            ActBusinessExtra extra = this.baseMapper.findBusinessExtraByBusinessId(actBusiness.getId(),1);
            if(extra == null){

                extra = new ActBusinessExtra();
                extra.setDepartCode(code);
                extra.setUserIds(String.join(",", names));
                extra.setCreateBy(sysUser.getUsername());
                extra.setPostponeFlag(actBusiness.getPostponeFlag());
                extra.setProcSort(1);
                extra.setBusinessId(actBusiness.getId());
                extra.setProcInstId(actBusiness.getProcInstId());
                extra.setRegisterFlag(actBusiness.getReFlag());
                extra.setTaskKey(task.getTaskDefinitionKey());
                if(isSpeed){
                    extra.setSpeedStatus(1);
                }else{
                    extra.setSpeedStatus(0);
                }
                this.baseMapper.saveBusinessExtra(extra);
            }else{
                extra.setDepartCode(code);
                extra.setUserIds(String.join(",", names));
                extra.setCreateBy(sysUser.getUsername());
                extra.setProcSort(1);
                extra.setBusinessId(actBusiness.getId());
                extra.setProcInstId(actBusiness.getProcInstId());
                extra.setRegisterFlag(actBusiness.getReFlag());
                extra.setTaskKey(task.getTaskDefinitionKey());
                extra.setUpdateBy(sysUser.getUsername());
                extra.setUpdateTime(new Date());
                this.baseMapper.updateBusinessExtra(extra);
            }

        }
        //添加工单-流程关联表
        saveBusinessPro(actBusiness);
    }


    /**
     *
     * @param actBusiness
     */
    public void saveOtherExtra(ActBusiness actBusiness,Task task,List<LoginUser> userList){

        Map<String, List<LoginUser>> detailsMap = userList.stream()
                .collect(Collectors.groupingBy(LoginUser::getOrgCode));
        int max = this.baseMapper.findMaxSortByBusinessId(actBusiness.getId());
        for (Map.Entry<String, List<LoginUser>> entry : detailsMap.entrySet()) {
            String code = entry.getKey();
            ActBusinessExtra extra  = this.baseMapper.findExtraByBusinessIdAndTaskKeyAndDepartCode(actBusiness.getId(),task.getTaskDefinitionKey(),code);

            List<LoginUser> users = entry.getValue();

            List<String> names = users.stream().map(LoginUser::getUsername).collect(Collectors.toList());


            if(extra == null){
                extra = new ActBusinessExtra();
                extra.setDepartCode(code);
                extra.setUserIds(String.join(",", names));
                extra.setCreateBy(actBusiness.getCreateBy());
                extra.setPostponeFlag(actBusiness.getPostponeFlag());
                extra.setProcSort(max+1);
                extra.setBusinessId(actBusiness.getId());
                extra.setProcInstId(actBusiness.getProcInstId());
                extra.setRegisterFlag(0);
                extra.setSpeedStatus(0);
                extra.setTaskKey(task.getTaskDefinitionKey());
                this.baseMapper.saveBusinessExtra(extra);
            }else{
                LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
                extra.setDepartCode(code);
                extra.setUserIds(String.join(",", names));
                extra.setCreateBy(actBusiness.getCreateBy());
                extra.setBusinessId(actBusiness.getId());
                extra.setProcInstId(actBusiness.getProcInstId());
                extra.setRegisterFlag(actBusiness.getReFlag());
                extra.setTaskKey(task.getTaskDefinitionKey());
                extra.setUpdateBy(sysUser.getUsername());
                extra.setUpdateTime(new Date());
                this.baseMapper.updateBusinessExtra(extra);
            }
        }


    }


    public void updateExtraByBack(ActBusiness actBusiness,Task task,List<LoginUser> userList){
        Map<String, List<LoginUser>> detailsMap = userList.stream()
                .collect(Collectors.groupingBy(LoginUser::getOrgCode));

        for (Map.Entry<String, List<LoginUser>> entry : detailsMap.entrySet()) {
            String code = entry.getKey();
            ActBusinessExtra extra  = this.baseMapper.findExtraByBusinessIdAndTaskKeyAndDepartCode(actBusiness.getId(),task.getTaskDefinitionKey(),code);

            List<LoginUser> users = entry.getValue();

            List<String> names = users.stream().map(LoginUser::getUsername).collect(Collectors.toList());

            if(extra != null) {
                LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
                extra.setDepartCode(code);
                extra.setUserIds(String.join(",", names));
                extra.setCreateBy(actBusiness.getCreateBy());
                extra.setBusinessId(actBusiness.getId());
                extra.setProcInstId(actBusiness.getProcInstId());
                extra.setRegisterFlag(actBusiness.getReFlag());
                extra.setTaskKey(task.getTaskDefinitionKey());
                extra.setUpdateBy(sysUser.getUsername());
                extra.setProcStatus(0);
                extra.setUpdateTime(new Date());
                this.baseMapper.updateBusinessExtra(extra);
            }
        }
    }

    public void updateExtraByUnite(ActBusiness actBusiness,String taskId,List<LoginUser> userList){
        Map<String, List<LoginUser>> detailsMap = userList.stream()
                .collect(Collectors.groupingBy(LoginUser::getOrgCode));

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        int max = this.baseMapper.findMaxSortByBusinessId(actBusiness.getId());
        for (Map.Entry<String, List<LoginUser>> entry : detailsMap.entrySet()) {
            String code = entry.getKey();

            ActBusinessExtra extra  = this.baseMapper.findExtraByBusinessIdAndTaskKeyAndDepartCode(actBusiness.getId(),task.getTaskDefinitionKey(),code);

            List<LoginUser> users = entry.getValue();

            List<String> names = users.stream().map(LoginUser::getUsername).collect(Collectors.toList());

            if(extra != null) {
                LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
                extra.setDepartCode(code);
                extra.setUserIds(String.join(",", names));
                extra.setCreateBy(actBusiness.getCreateBy());
                extra.setBusinessId(actBusiness.getId());
                extra.setProcInstId(actBusiness.getProcInstId());
                extra.setRegisterFlag(actBusiness.getReFlag());
                extra.setTaskKey(task.getTaskDefinitionKey());
                extra.setUpdateBy(sysUser.getUsername());
                extra.setProcStatus(1);
                extra.setUpdateTime(new Date());
                this.baseMapper.updateBusinessExtra(extra);
            }else{
                extra = new ActBusinessExtra();
                extra.setDepartCode(code);
                extra.setUserIds(String.join(",", names));
                extra.setCreateBy(actBusiness.getCreateBy());
                extra.setPostponeFlag(actBusiness.getPostponeFlag());
                extra.setProcSort(max);
                extra.setBusinessId(actBusiness.getId());
                extra.setProcInstId(actBusiness.getProcInstId());
                extra.setRegisterFlag(0);
                extra.setSpeedStatus(0);
                extra.setTaskKey(task.getTaskDefinitionKey());
                this.baseMapper.saveBusinessExtra(extra);
            }
        }
    }


    public void updateExtra(String businessId,String key, String orgCode){
        ActBusinessExtra extra  = this.baseMapper.findExtraByBusinessIdAndTaskKeyAndDepartCode(businessId,key,orgCode);
        extra.setProcStatus(1);
        extra.setUpdateTime(new Date());
        this.baseMapper.updateBusinessExtra(extra);
    }


    /**
     * 添加委托任务的额外数据扩展表
     * @param taskId
     * @param user
     */
    public void saveExtraByDelegate(ActBusiness actBusiness,String taskId,LoginUser user){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ActBusinessExtra extra  = this.baseMapper.findExtraByBusinessIdAndTaskKeyAndDepartCode(actBusiness.getId(),task.getTaskDefinitionKey(),user.getOrgCode());
        extra.setDepartCode(user.getOrgCode());
        extra.setUserIds(user.getUsername());
        extra.setProcStatus(0);
        baseMapper.updateBusinessExtra(extra);
    }

    public void saveBusinessPro(ActBusiness actBusiness){
        this.baseMapper.updateBusinessPro(actBusiness.getId());
        ActBusinessPro pro = new ActBusinessPro();
        pro.setBusinessId(actBusiness.getId());
        pro.setProcInstId(actBusiness.getProcInstId());
        pro.setCreateBy(actBusiness.getCreateBy());
        pro.setLatest(1);
        this.baseMapper.saveBusinessPro(pro);
    }

    public void saveBusinessVisit(ActBusinessVisit visit){
        this.baseMapper.saveBusinessVisit(visit);
        this.baseMapper.updateLatestVisit(visit.getId());
    }

    public List<ActBusinessVisit> getVisitList(String procInstId){
        return this.baseMapper.getVisitList(procInstId);
    }

    public int findTotalBusinessNum(){
        return this.baseMapper.findTotalBusinessNum();
    }

    public List<ActChartVo> findMyBusinessIdNum(Map<String,Object> map){

        return this.baseMapper.findMyBusinessIdNum(map);
    }

    public List<ActChartVo> findNewBusinessNum(Map<String,Object> map){

        return this.baseMapper.findNewBusinessNum(map);
    }

    public List<ActChartVo> findNoHandleBusinessNum(Map<String,Object> map){

        return this.baseMapper.findNoHandleBusinessNum(map);
    }

    public void saveTaskAway(String username,Task task,ActBusiness actBusiness){
        ActHiTaskAway actHiTaskAway = new ActHiTaskAway();
        actHiTaskAway.setCreateBy(username);
        actHiTaskAway.setCreateTime(new Date());
        actHiTaskAway.setTaskId(task.getId());
        actHiTaskAway.setBusinessId(actBusiness.getId());
        actHiTaskAway.setTaskTime(task.getCreateTime());
        this.baseMapper.saveTaskAway(actHiTaskAway);
    }

    public void saveTaskSupervise(ActHiTaskSupervise supervise){
        this.baseMapper.saveTaskSupervise(supervise);
    }

    public List<ActHiTaskAway> findTaskAway(String businessId,String taskId,String userId){
        return  this.baseMapper.findTaskAway(businessId, taskId,userId);
    }


    Integer status_refuse = 3;  //驳回
    Integer status_end = 2;     //完成
    Integer status_ing = 1;     //进行中
    Integer status_apply = 0;   //未申请

    Integer visit_no = 0; //未回访
    Integer visit_yes = 1; //已回访

    public List<ActProcessData> buildActProcessMap(String orgCode, String beginTime, String endTime, String pageNo, String pageSize){

        List<ActProcessData> dataList = new ArrayList<>();

        Map<String,Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(orgCode)){
            map.put("departCode",orgCode);
        }
        map.put("beginTime",beginTime);
        map.put("endTime",endTime);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        List<String> roleByUserName = actNodeService.getRoleByUserName(sysUser.getUsername());
        if(!roleByUserName.contains("admin")){
            map.put("userId",sysUser.getUsername());
        }

        boolean rootFlag;

        SysDepartModel currentDepart = sysBaseAPI.getDepartByCode(sysUser.getOrgCode());

        if(StringUtils.isNotBlank(currentDepart.getParentId())){
            rootFlag = false;
        }else{
            rootFlag = true;
        }

        List<SysDepartModel> departModelList = sysBaseAPI.getDepartsBySubDepCode(sysUser.getOrgCode());
        List<String> codes = departModelList.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
        map.put("departCodes",codes);

        List<ProcDataVo> procDataVos = this.baseMapper.selectProcDataTable(map);

        //待回访的单
        int visitNum = 0;
        //进行中的单
        int ingNum = 0;
        //正在进行的正常单
        int instNormalNum = 0;
        //正在进行的重办单
        int instReNum = 0;
        //正在进行的逾期单
        int instDueNum = 0;
        //正在进行的重办-逾期单
        int instReAndDueNum = 0;

        //已完成的单
        int doneNum = 0;
        //已完成的正常单
        int doneNormalNum = 0;
        //已经结束的重办单
        int doneReNum = 0;
        //已完成的逾期单
        int doneDueNum = 0;
        //已完成的逾期-重办单
        int doneReAndDueNum = 0;

        int refuseNum = 0;

        Set<String> totalSet = new HashSet<>();

        for (int i = 0; i < procDataVos.size(); i++) {
            ProcDataVo procDataVo = procDataVos.get(i);
            if(!totalSet.contains(procDataVo.getBusinessId())){

                //判断是否重办
                int re = this.baseMapper.findReNumByBusinessId(procDataVo.getBusinessId());

                //判断是否逾期
                int dueFlag = this.baseMapper.findDueByBusinessId(procDataVo.getBusinessId());

                totalSet.add(procDataVo.getBusinessId());
                if(status_ing.equals(procDataVo.getStatus())){
                    //进行中的单
                    ingNum+=1;

                    //正在进行的重办-逾期单
                    if(re > 1 && dueFlag >0){
                        instReAndDueNum+=1;
                    }else if(re > 1){
                        //进行中的重办单
                        instReNum+=1;
                    }else if(dueFlag > 0){
                        //进行中的逾期单
                        instDueNum+=1;
                    }else{
                        instNormalNum+=1;
                    }
                }
                //判断工单已完成并是否回访
                else if(status_end.equals(procDataVo.getStatus())){

                    if(visit_no.equals(procDataVo.getVisitFlag())){
                        visitNum+=1;
                    }else{
                        doneNum+=1;
                        //正在进行的重办-逾期单
                        if(re > 1 && dueFlag > 0){
                            doneReAndDueNum+=1;
                        }else if(re > 1){
                            //已完成的重办单
                            doneReNum+=1;
                        }else if(dueFlag > 0){
                            //已完成的逾期单
                            doneDueNum+=1;
                        }else{
                            doneNormalNum+=1;
                        }
                    }
                }
                else if(status_refuse.equals(procDataVo.getStatus())){
                    refuseNum+=1;
                }
            }
        }

        ActProcessData vo = new ActProcessData();
        vo.setDepartName("总计");
        //总计
        vo.setDepartCode("all");
        vo.setTotalNum(totalSet.size());
        vo.setVisitNum(visitNum);
        vo.setInstNum(ingNum);
        vo.setInstReNum(instReNum);
        vo.setInstDueNum(instDueNum);
        vo.setInstNormalNum(instNormalNum);
        vo.setInstReAndDueNum(instReAndDueNum);
        vo.setDoneNum(doneNum);
        vo.setDoneReNum(doneReNum);
        vo.setDoneDueNum(doneDueNum);
        vo.setDoneNormalNum(doneNormalNum);
        vo.setDoneReAndDueNum(doneReAndDueNum);
        vo.setRefuseNum(refuseNum);
        TaskQuery query = taskService.createTaskQuery().taskCandidateOrAssigned(sysUser.getUsername());
        List<Task> tasks = query.list();

        vo.setTodoNum(tasks.size());
        //加入到第一位
        dataList.add(0,vo);

        //是否是根部门
        if(rootFlag){

            Map<ActProcessData,List<ProcDataVo>> rootMap = new HashMap<>();
            if(departModelList.size()>0){
                for (int i = 0; i < departModelList.size(); i++) {
                    SysDepartModel d = departModelList.get(i);
                    ActProcessData ad = new ActProcessData(d.getOrgCode(),d.getDepartName());
                    if(StringUtils.isBlank(d.getParentId())){
                        List<ProcDataVo> items = procDataVos.stream().filter(v -> v.getDepartCode().equals(d.getOrgCode())).collect(Collectors.toList());
                        rootMap.put(ad,items);
                    }else if(d.getParentId().equals(currentDepart.getId())){
                        ad.setHasChildren(1);
                        List<ProcDataVo> items = procDataVos.stream().filter(v -> v.getParentId().equals(d.getId()) || v.getDepartId().equals(d.getId())).collect(Collectors.toList());
                        rootMap.put(ad,items);
                    }

                }
            }

            for (Map.Entry<ActProcessData, List<ProcDataVo>> entry : rootMap.entrySet()) {
                ActProcessData d = analysisData(entry.getKey(),entry.getValue());
                dataList.add(d);
            }
        }else{

            Map<String,List<ProcDataVo>> collect = procDataVos.stream().collect(Collectors.groupingBy(ProcDataVo::getDepartCode));

            for (Map.Entry<String, List<ProcDataVo>> entry : collect.entrySet()) {
                ActProcessData d = new ActProcessData(entry.getKey(),"");
                d = analysisData(d,entry.getValue());
                dataList.add(d);
            }

        }
        int index = 0;
        if(dataList.size()>2){
            for (int i = 0; i < dataList.size(); i++) {
                if(sysUser.getOrgCode().equals(dataList.get(i).getDepartCode())){
                    index = i;
                    break;
                }
            }
            Collections.swap(dataList, index, 1);
        }
        return dataList;
    }

    public List<ActProcessData> getChildProcessMap(String orgCode,String beginTime,String endTime){
        List<ActProcessData> dataList = new ArrayList<>();

        Map<String,Object> map = new HashMap<>();

        map.put("beginTime",beginTime);
        map.put("endTime",endTime);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        map.put("userId",sysUser.getUsername());

        SysDepartModel currentDepart = sysBaseAPI.getDepartByCode(orgCode);

        List<SysDepartModel> departModelList = sysBaseAPI.getDepartsBySubDepCode(orgCode);
        List<String> codes = departModelList.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
        map.put("departCodes",codes);

        List<ProcDataVo> procDataVos = this.baseMapper.selectProcDataTable(map);

        Map<ActProcessData,List<ProcDataVo>> rootMap = new HashMap<>();
        if(departModelList.size()>0){
            for (int i = 0; i < departModelList.size(); i++) {
                SysDepartModel d = departModelList.get(i);
                ActProcessData ad = new ActProcessData(d.getOrgCode(),"  " +d.getDepartName());
                List<ProcDataVo> items = procDataVos.stream().filter(v -> v.getDepartId().equals(d.getId())).collect(Collectors.toList());
                rootMap.put(ad,items);
            }
        }

        for (Map.Entry<ActProcessData, List<ProcDataVo>> entry : rootMap.entrySet()) {
            ActProcessData d = analysisData(entry.getKey(),entry.getValue());
            dataList.add(d);
        }

        int index = 0;
        if(dataList.size() > 0){
            for (int i = 0; i < dataList.size(); i++) {
                if(currentDepart.getOrgCode().equals(dataList.get(i).getDepartCode())){
                    index = i;
                    break;
                }
            }
            Collections.swap(dataList, index, 0);
        }

        return dataList;
    }

    private ActProcessData analysisData(ActProcessData vo,List<ProcDataVo> vos){

        //待回访的单
        int visitNum = 0;
        //进行中的单
        int ingNum = 0;
        //正在进行的正常单
        int instNormalNum = 0;
        //正在进行的重办单
        int instReNum = 0;
        //正在进行的逾期单
        int instDueNum = 0;
        //正在进行的重办-逾期单
        int instReAndDueNum = 0;

        //已完成的单
        int doneNum = 0;
        //已完成的正常单
        int doneNormalNum = 0;
        //已经结束的重办单
        int doneReNum = 0;
        //已完成的逾期单
        int doneDueNum = 0;
        //已完成的逾期-重办单
        int doneReAndDueNum = 0;

        int refuseNum = 0;

        //通过工单ID对工单去重
        vos = vos.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ProcDataVo::getBusinessId))), ArrayList::new));

        for (int i = 0; i < vos.size(); i++) {
            ProcDataVo procDataVo = vos.get(i);
            if(StringUtils.isBlank(vo.getDepartName())){
                vo.setDepartName(procDataVo.getDepartName());
            }
            //判断是否重办
            int re = this.baseMapper.findReNumByBusinessId(procDataVo.getBusinessId());

            //判断是否逾期
            int dueFlag = this.baseMapper.findDueByBusinessId(procDataVo.getBusinessId());

            if(status_ing.equals(procDataVo.getStatus())) {
                //进行中的单
                ingNum += 1;

                //正在进行的重办-逾期单
                if (re > 1 && dueFlag > 0) {
                    instReAndDueNum += 1;
                } else if (re > 1) {
                    //进行中的重办单
                    instReNum += 1;
                } else if (dueFlag > 0) {
                    //进行中的逾期单
                    instDueNum += 1;
                } else {
                    instNormalNum += 1;
                }
            }
            //判断工单已完成并是否回访
            else if(status_end.equals(procDataVo.getStatus())){


                if(visit_no.equals(procDataVo.getVisitFlag())){
                    visitNum+=1;
                }else{
                    doneNum+=1;
                    //正在进行的重办-逾期单
                    if(re > 1 && dueFlag >0){
                        doneReAndDueNum+=1;
                    }else if(re > 1){
                        //已完成的重办单
                        doneReNum+=1;
                    }else if(dueFlag > 0){
                        //已完成的逾期单
                        doneDueNum+=1;
                    }else{
                        doneNormalNum+=1;
                    }
                }
            }
            else if(status_refuse.equals(procDataVo.getStatus())){
                refuseNum+=1;
            }
        }
        //总计
        vo.setTotalNum(vos.size());
        vo.setVisitNum(visitNum);
        vo.setInstNum(ingNum);
        vo.setInstReNum(instReNum);
        vo.setInstDueNum(instDueNum);
        vo.setInstNormalNum(instNormalNum);
        vo.setInstReAndDueNum(instReAndDueNum);
        vo.setDoneNum(doneNum);
        vo.setDoneReNum(doneReNum);
        vo.setDoneDueNum(doneDueNum);
        vo.setDoneNormalNum(doneNormalNum);
        vo.setDoneReAndDueNum(doneReAndDueNum);
        vo.setRefuseNum(refuseNum);
        return vo;
    }

    public ActBusiness getOneBean(LambdaQueryWrapper<ActBusiness> wrapper){

        List<ActBusiness> actNodeList = this.list(wrapper);
        if(actNodeList.size()>0){
            return actNodeList.get(0);
        }else{
            return new ActBusiness();
        }
    }

    //数据分析
    public IPage<ActUserQueryData>  getBusinessOperationQuery(String name,String beginTime,String endTime, String pageNo, String pageSize,Integer operaType){

        Map<String,Object> query = new HashMap<>();

        if(StringUtils.isBlank(pageNo)){
            pageNo = "1";
        }
        if(StringUtils.isBlank(pageSize)){
            pageSize = "10";
        }
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);
        query.put("beginTime",beginTime);
        query.put("endTime",endTime);
        query.put("name",name);

        if(operaType != null){
            String value = getOperaToString(operaType);
            if(StringUtils.isNotBlank(value)){
                query.put("operaType",value);
            }
        }

        LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        query.put("username",sysUser.getUsername());
        List<ActUserQueryData> data = this.baseMapper.findBusinessOperationByUser(query);
        long total = this.baseMapper.findTotalBusinessOperationByUser(query);

        IPage<ActUserQueryData> page = new Page<>();
        page.setTotal(total);
        page.setRecords(data);

        return page;
    }

    private String getOperaToString(int operaType){
        String value = "";
        switch (operaType){
            case 1:
                value = "starter";
                break;
            case 2:
                value = "actualExecutor_b";
                break;
            case 3:
                value = "urgent";
                break;
            case 4:
                value = "supervise";
                break;
            case 5:
                break;
            case 6:
                value = "unite";
                break;
            case 7:
                value = "unite_agree";
                break;
            case 8:
                break;
            case 9:
                value = "postpone";
                break;
            case 10:
                value = "actualExecutor_p";
                break;
            default:
                value = "";
                break;
        }
        return value;
    }

    public List<ActQuantizaData> getUnitData(String departCode,String beginTime,String endTime,int timeType){

        Map<String,Object> query = new HashMap<>();

        LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> departs = sysBaseAPI.getDepartsBySubDepCode(sysUser.getOrgCode());

        if(StringUtils.isNotBlank(departCode)){

            List<String> codes = departs.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());

            if(!codes.contains(departCode)){
                departCode = sysUser.getOrgCode();
            }
        }else{
            departCode = sysUser.getOrgCode();
        }

        SysDepartModel depart = sysBaseAPI.getDepartByCode(departCode);

        List<SysDepartModel> _ds = departs.stream().filter(d -> d.getParentId().equals(depart.getId()) || d.getId().equals(depart.getId())).collect(Collectors.toList());
        List<ActQuantizaData> dataList = new ArrayList<>();
        for (int i = 0; i < _ds.size(); i++) {

            if(timeType != 0){
                getBeginAndEndTimeByType(query,timeType);
            }else{
                query.put("beginTime",beginTime);
                query.put("endTime",endTime);
            }

            SysDepartModel d = _ds.get(i);
            ActQuantizaData data = new ActQuantizaData();
            data.setDepartCode(d.getOrgCode());
            data.setDepartName(d.getDepartName());

            if(d.getOrgCode().equals(departCode)){
                query.put("departCode",d.getOrgCode());
                query.remove("departId");
            }else{
                query.remove("departCode");
                query.put("departId",d.getId());
            }

            //处理工单量
            int handleNum = this.baseMapper.findHandleNumByDepart(query);

            data.setTotalNum(handleNum);
            data.setHandleNum(handleNum);

            //待接收工单

            int todoNum = this.baseMapper.findTodoNumByDepart(query);
            data.setTodoNum(todoNum);

            //及时接受工单量
            query.put("awayTime",ActivitiConstant.PROCESS_AWAY_TIME);
            int awayNum = this.baseMapper.findAwayNumByDepart(query);
            data.setAwayNum(awayNum);
            data.setUnAwayNum(handleNum-awayNum);

            //被督办次数
            int superviseNum = this.baseMapper.findSuperviseByDepart(query);
            data.setSuperviseNum(superviseNum);
            //申请延期量
            int postpone = this.baseMapper.findPostponeNumByDepart(query);
            data.setPostponeNum(postpone);

            //逾期量
            int dueNum = this.baseMapper.findDueNumByDepart(query);
            data.setDueNum(dueNum);

            //重办
            int reNum = this.baseMapper.findReNumByDepart(query);
            data.setReNum(reNum);

            //退回
            query.put("operaType",ActivitiConstant.EXECUTOR_TYPE_b);
            int refuseNum = this.baseMapper.findRefuseNumByDepart(query);
            data.setRefuseNum(refuseNum);

            //平均受理时长(毫秒)
            int avgHandleTime = this.baseMapper.findAvgHandleTimeByDepart(query);
            data.setAverageTime(avgHandleTime);

            //平均接收时长(秒)
            int avgAwayTime = this.baseMapper.findAvgAwayTimeByDepart(query);
            data.setAverageAwayTime(avgAwayTime);

            //正常单
            int normalNum = this.baseMapper.findNormalNumByDepart(query);
            data.setNormalNum(normalNum);

            //结办单
            query.put("visitFlag",1);
            int doneNum = this.baseMapper.findNormalNumByDepart(query);
            data.setDoneNum(doneNum);

            //下派单
            query.put("departId",d.getId());
            int underNum = this.baseMapper.findUnderNumByDepart(query);
            data.setUnderNum(underNum);

            query.clear();
            dataList.add(data);
        }

        int index = 0;
        if(dataList.size() > 0){
            for (int i = 0; i < dataList.size(); i++) {
                if(depart.getOrgCode().equals(dataList.get(i).getDepartCode())){
                    index = i;
                    break;
                }
            }
            Collections.swap(dataList, index, 0);
        }
        return dataList;
    }


    public void getBeginAndEndTimeByType(Map<String,Object> query,int timeType){

        //1:昨天 2:本周 3:本月 4:本年
        if(timeType == 1){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
            Date today = calendar.getTime();
            query.put("beginTime",getStartOfDay(today));
            query.put("endTime",getEndOfDay(today));
        }else if(timeType == 2){
            Calendar cal=Calendar.getInstance();
            cal.add(Calendar.WEEK_OF_MONTH, 0);
            cal.set(Calendar.DAY_OF_WEEK, 2);
            Date time=cal.getTime();
            query.put("beginTime",getStartOfDay(time));
        }else if(timeType == 3){
            Calendar cal=Calendar.getInstance();
            cal.add(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Date time=cal.getTime();
            query.put("beginTime",getStartOfDay(time));
        }else if(timeType == 4){
            String time = new SimpleDateFormat("yyyy").format(new Date())+"-01-01 00:00:00";
            query.put("beginTime",time);
        }

    }


    //外部用户查询工单
    public List<ActApplyUserFormVo> findApplyUserFormVo(String name,String mobile){
        List<ActApplyUserFormVo> list = this.baseMapper.findApplyUserFormVo();
        List<ActApplyUserFormVo> vos = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            int check = this.baseMapper.checkedUserForm(list.get(i).getTableName(),list.get(i).getTableId(),name,mobile);
            if(check > 0){
                vos.add(list.get(i));
            }
        }
        return vos;
    }


    //调度中心数据
    public List<ActCenterData> getCenterListData(String beginTime,String endTime,int timeType){
        Map<String,Object> query = new HashMap<>();
        if(timeType != 0){
            getBeginAndEndTimeByType(query,timeType);
        }else{
            query.put("beginTime",beginTime);
            query.put("endTime",endTime);
        }
        query.put("roleCode","supervisor");
        List<ActCenterData> dataList = this.baseMapper.findDoneNumByCenterGroupUser(query);

        int todo = this.baseMapper.findTodoNumByCenter(query);

        for (int i = 0; i < dataList.size(); i++) {
            ActCenterData data = dataList.get(i);
            query.put("applyBy",data.getUsername());
            int refuseNum = this.baseMapper.findRefuseNumByCenter(query);
            data.setRefuseNum(refuseNum);
            int refusedNum = this.baseMapper.findRefusedNumByCenter(query);
            data.setRefusedNum(refusedNum);

            ActChartVo vo = this.baseMapper.findAvgTimeByCenter(query);

            if(vo != null){
                int avgTime = (int) (Double.parseDouble(vo.getTime())/Double.parseDouble(vo.getText()));
                data.setAverageTime(avgTime);
            }
            data.setTodoNum(todo);

            int timeLimit = ActivitiConstant.PROCESS_AWAY_TIME * 86400;
            query.put("passTime",timeLimit);
            int passNum = this.baseMapper.findPassNumByCenter(query);
            data.setPassNum(passNum);
        }


        return dataList;
    }

    //回访数据
    public List<ActVisitData> getVisitData(String beginTime,String endTime,int timeType){
        Map<String,Object> query = new HashMap<>();
        if(timeType != 0){
            getBeginAndEndTimeByType(query,timeType);
        }else{
            query.put("beginTime",beginTime);
            query.put("endTime",endTime);
        }
        List<ActVisitData> dataList = this.baseMapper.findVisitData(query);

        for (int i = 0; i < dataList.size(); i++) {
            ActVisitData data = dataList.get(i);
            query.put("visitor",data.getUsername());
            int reNum = this.baseMapper.findReVisitNumByUser(query);
            data.setReVisitNum(reNum);

            List<ActChartVo> times = this.baseMapper.findAvgTimeByUser(query);

            //取工单最低的那个回访时间差
            Map<String, ActChartVo> configMap = times.parallelStream().collect(
                        Collectors.groupingBy(ActChartVo::getText,
                                Collectors.collectingAndThen(Collectors.reducing(( c1,  c2) -> Integer.parseInt(c1.getTime()) < Integer.parseInt(c2.getTime()) ? c1 : c2), Optional::get)));

            int timeLimit = ActivitiConstant.PROCESS_AWAY_TIME * 86400;

            int maxTime = 0;

            int passNum = 0;
            for (Map.Entry<String, ActChartVo> entry : configMap.entrySet()) {
                ActChartVo vo = entry.getValue();

                int time = Integer.parseInt(vo.getTime());
                maxTime+=time;
                if(time<=timeLimit){
                    passNum+=1;
                }
            }

            data.setAvgTime(maxTime/data.getVisitBusinessNum());
            data.setPassNum(passNum);
        }

        return dataList;
    }

    //
    public int findNoVisitNum(String beginTime,String endTime,int timeType){

        Map<String,Object> query = new HashMap<>();
        if(timeType != 0){
            getBeginAndEndTimeByType(query,timeType);
        }else{
            query.put("beginTime",beginTime);
            query.put("endTime",endTime);
        }
        int num = this.baseMapper.findNoVisitNum(query);
        return num;
    }
    public Map<String,int[]> getVisitRankData(String beginTime,String endTime,int timeType){

        Map<String,int[]> dataMap = new HashMap<>();
        Map<String,Object> query = new HashMap<>();
        if(timeType != 0){
            getBeginAndEndTimeByType(query,timeType);
        }else{
            query.put("beginTime",beginTime);
            query.put("endTime",endTime);
        }

        int [] ranks = new int[]{0,0,0,0,0};
        List<ActRankData> rankData = this.baseMapper.findVisitRank(query);
        for (int i = 0; i < rankData.size(); i++) {
            ranks[Integer.parseInt(rankData.get(i).getRank())-1] = rankData.get(i).getNum();
        }
        dataMap.put("回访人员满意度",ranks);

        dataMap.put("坐席人员满意度",new int[]{0,0,0,0,0});
        int [] userRanks = new int[]{0,0,0,0,0};

        List<ActApplyUserFormVo> list = this.baseMapper.findApplyUserFormVo();
        for (int i = 0; i < list.size(); i++) {
            ActApplyUserFormVo vo = list.get(i);
            if(vo.getComplainFlag() == 1){
                userRanks[Integer.parseInt(vo.getComplainRank())-1] += 1;
            }
        }
        dataMap.put("群众评价满意度",userRanks);
        return dataMap;
    }


    public Map<String,Object> getBusinessStatusList(){
        String code = "complain_type";
        Map<String,Object> dataMap = new HashMap<>();

        List<DictModel> dictModels = sysBaseAPI.queryDictItemsByCode(code);
        int length = dictModels.size();

        List<ActChartVo> vos = this.baseMapper.findBusinessStatusList();

        String[] texts = {"status-todo","status-ing","status-done","status-visited"};

        for (int i = 0; i < texts.length; i++) {
            String text = texts[i];
            long[] numList = new long[length];
            String status = i+"";
            for (int j = 0; j < dictModels.size(); j++) {
                String name = dictModels.get(j).getText();
                long num = vos.stream().filter(v -> status.equals(v.getTime()) && name.equals(v.getText())).count();
                numList[j] = num;

            }
            dataMap.put(text,numList);
        }
        long[] sumList = new long[length];
        String[] textList = new String[length];
        for (int i = 0; i < dictModels.size(); i++) {
            String name = dictModels.get(i).getText();
            long num = vos.stream().filter(v -> name.equals(v.getText())).count();
            sumList[i] = num;
            textList[i] = name;
        }
        dataMap.put("status-sum",sumList);
        dataMap.put("title",textList);
        return dataMap;
    }


    public static String getEndOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());;
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant()));
    }


    public static String getStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()));
    }


}

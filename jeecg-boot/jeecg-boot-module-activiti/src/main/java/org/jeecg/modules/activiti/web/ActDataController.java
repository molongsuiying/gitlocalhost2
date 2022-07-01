package org.jeecg.modules.activiti.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.TaskQuery;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.activiti.data.*;
import org.jeecg.modules.activiti.service.Impl.ActBusinessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@RestController
@RequestMapping("/actData")
@Slf4j
@Transactional
@Api(tags="数据")
public class ActDataController {

    @Autowired
    ISysBaseAPI sysBaseAPI;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    ActBusinessServiceImpl actBusinessService;


    @AutoLog(value = "数据-工单列表")
    @ApiOperation(value="流程-工单列表", notes="工单列表")
    @RequestMapping(value = "/getDataListTop", method = RequestMethod.GET)
    public Result getDataListTop(){


        Map<String,Object> dataMap = new HashMap<>();


        //用户总量
        long userNum = sysBaseAPI.countUserList();

        dataMap.put("userNum",userNum);
        //工单总量

        long taskNum = actBusinessService.findTotalBusinessNum();
        dataMap.put("taskNum",taskNum);
        //待办总量
        ProcessInstanceQuery InstQuery = runtimeService.createProcessInstanceQuery()
                .orderByProcessInstanceId().desc();

        long todoNum = InstQuery.count();
        dataMap.put("todoNum",todoNum);
        //个人待办
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getUsername();
        TaskQuery taskQuery = taskService.createTaskQuery().taskCandidateOrAssigned(userId);

        long ownNum = taskQuery.count();
        dataMap.put("ownNum",ownNum);
        return Result.ok(dataMap);
    }


    @AutoLog(value = "数据-工单列表")
    @ApiOperation(value="流程-工单列表", notes="工单列表")
    @RequestMapping(value = "/getDataListByBigScreen", method = RequestMethod.GET)
    public Result getDataListByBigScreen(){


        Map<String,Object> dataMap = new HashMap<>();


        //用户总量
        long userNum = sysBaseAPI.countUserList();

        dataMap.put("userNum",userNum);
        //工单总量

        long taskNum = actBusinessService.findTotalBusinessNum();
        dataMap.put("taskNum",taskNum);
        //待办总量
        ProcessInstanceQuery InstQuery = runtimeService.createProcessInstanceQuery()
                .orderByProcessInstanceId().desc();

        long todoNum = InstQuery.count();
        dataMap.put("todoNum",todoNum);

        return Result.ok(dataMap);
    }


    @AutoLog(value = "数据-最近7天工单数据")
    @ApiOperation(value="数据-最近7天工单数据", notes="最近7天工单数据")
    @RequestMapping(value = "/getDataListWeek", method = RequestMethod.GET)
    public Result getDataListWeek(@ApiParam(value = "past",required = false)int past,
                                  @ApiParam(value = "activeKey",required = false)int activeKey){

        if(past == 0){
            past = 7;
        }
        if(activeKey == 0){
            activeKey = 1;
        }
        Map<String,Object> dataMap = new HashMap<>();

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getUsername();
        try {
            //获取past 天前
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
            Date today = calendar.getTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String result = format.format(today);

            Date afterTime = format.parse(result);

            if(activeKey == 1){
                Map<String,Object> query = new HashMap<>();
                query.put("beginTime",result);
                query.put("userId",userId);
                List<ActChartVo> list = actBusinessService.findMyBusinessIdNum(query);
                Map<Object, List<ActChartVo>> groupByDay = list.stream().collect(
                        Collectors.groupingBy(ActChartVo::getTime));
                List<Map<String,Object>> dataList = new ArrayList<>();
                groupByDay.forEach((k, v) -> {

                    Map<String,Object> map = new HashMap<>();
                    map.put(k.toString(),v.size());
                    dataList.add(map);
                });
                dataMap.put("doneList",dataList);
            }else if(activeKey == 2){
                HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().taskCreatedAfter(afterTime)
                        .or().taskCandidateUser(userId).taskAssignee(userId).endOr();

                List<HistoricTaskInstance> taskList = query.list();
                Map<String, List<HistoricTaskInstance>> groupByDay = taskList.stream().collect(
                        Collectors.groupingBy(t -> parse_yyyyMMdd(t.getCreateTime())));
                List<Map> dataList = new ArrayList<>();
                groupByDay.forEach((k, v) -> {

                    Map<String,Object> map = new HashMap<>();
                    map.put(k,v.size());
                    dataList.add(map);
                });
                dataMap.put("doneList",dataList);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //已办理

        return Result.ok(dataMap);
    }

    @AutoLog(value = "数据-最近7天工单数据")
    @ApiOperation(value="数据-最近7天工单数据", notes="最近7天工单数据")
    @RequestMapping(value = "/getDataListWeekByBigScreen", method = RequestMethod.GET)
    public Result getDataListWeekByBigScreen(@ApiParam(value = "past",required = false)int past){

        if(past == 0){
            past = 7;
        }
        Map<String,Object> dataMap = new HashMap<>();

        try {
            //获取past 天前
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
            Date today = calendar.getTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String result = format.format(today);

            Date afterTime = format.parse(result);


            Map<String,Object> query = new HashMap<>();
            query.put("beginTime",result);

            List<ActChartVo> list = actBusinessService.findMyBusinessIdNum(query);
            Map<Object, List<ActChartVo>> groupByDay = list.stream().collect(
                    Collectors.groupingBy(ActChartVo::getTime));
            List<Map<String,Object>> dataList = new ArrayList<>();
            groupByDay.forEach((k, v) -> {

                Map<String,Object> map = new HashMap<>();
                map.put(k.toString(),v.size());
                dataList.add(map);
            });
            dataMap.put("doneList",dataList);


            HistoricTaskInstanceQuery query2 = historyService.createHistoricTaskInstanceQuery().taskCreatedAfter(afterTime);
            List<HistoricTaskInstance> taskList = query2.list();
            Map<String, List<HistoricTaskInstance>> groupByDay2 = taskList.stream().collect(
                    Collectors.groupingBy(t -> parse_yyyyMMdd(t.getCreateTime())));
            List<Map> dataList2 = new ArrayList<>();
            groupByDay2.forEach((k, v) -> {

                Map<String,Object> map = new HashMap<>();
                map.put(k,v.size());
                dataList2.add(map);
            });
            dataMap.put("doneList2",dataList2);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        //已办理

        return Result.ok(dataMap);
    }

    @AutoLog(value = "数据-工单表格")
    @ApiOperation(value="数据-工单表格", notes="工单表格")
    @RequestMapping(value = "/getDataTable", method = RequestMethod.GET)
    public Result getDataTable(@ApiParam(value = "部门名称" )String departCode,
                                                     @ApiParam(value = "开始时间")String beginTime,
                                                     @ApiParam(value = "结束时间")String endTime,
                                                     @ApiParam(value = "当前页")String pageNo,
                                                     @ApiParam(value = "页数")String pageSize){

        List<ActProcessData> dataList = actBusinessService.buildActProcessMap(departCode,beginTime,endTime,pageNo,pageSize);

        return Result.ok(dataList);
    }

    @AutoLog(value = "数据-工单展开表格")
    @ApiOperation(value="数据-工单展开表格", notes="工单展开表格")
    @RequestMapping(value = "/getChildTable", method = RequestMethod.GET)
    public Result getChildTable(@ApiParam(value = "部门名称" )String departCode,
                               @ApiParam(value = "开始时间")String beginTime,
                               @ApiParam(value = "结束时间")String endTime){

        List<ActProcessData> dataList = actBusinessService.getChildProcessMap(departCode,beginTime,endTime);

        return Result.ok(dataList);
    }


    @AutoLog(value = "数据-量化统计")
    @ApiOperation(value="数据-量化统计", notes="量化统计")
    @RequestMapping(value = "/getQuantizationData", method = RequestMethod.GET)
    public Result getQuantizationData(  @ApiParam(value = "部门名称" )String departCode,
                                        @ApiParam(value = "开始时间")String beginTime,
                                        @ApiParam(value = "结束时间")String endTime,
                                        @ApiParam(value = "时间类型")int timeType){

        List<ActQuantizaData> dataList = actBusinessService.getUnitData(departCode,beginTime,endTime,timeType);
        IPage<ActQuantizaData> page = new Page<>();
        page.setRecords(dataList);
        page.setTotal(dataList.size());
        return Result.ok(page);
    }

    @AutoLog(value = "数据-工单状态列表")
    @ApiOperation(value="数据-工单状态列表", notes="工单状态列表")
    @RequestMapping(value = "/getDataStatusList", method = RequestMethod.GET)
    public Result getDataStatusList(){

        Map<String,Object> dataMap = new HashMap<>();

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getUsername();

        //待办理
        TaskQuery taskQuery = taskService.createTaskQuery().taskCandidateOrAssigned(userId);
        long ownNum = taskQuery.count();
        dataMap.put("ownNum",ownNum);
        //已办理
        HistoricTaskInstanceQuery query2 = historyService.createHistoricTaskInstanceQuery().or().taskCandidateUser(userId).
                taskAssignee(userId).endOr();
        long doneNum = query2.count();
        dataMap.put("doneNum",doneNum);
        //进行中
        HistoricProcessInstanceQuery query3 = historyService.createHistoricProcessInstanceQuery().startedBy(userId).unfinished();
        long instNum = query3.count();
        dataMap.put("instNum",instNum);

        //已结束
        HistoricProcessInstanceQuery query4 = historyService.createHistoricProcessInstanceQuery().startedBy(userId).or().finished().
                orderByProcessInstanceEndTime().desc();

        long endNum = query4.count();
        dataMap.put("endNum",endNum);


        return Result.ok(dataMap);
    }

    /*调度中心数据*/
    @AutoLog(value = "数据-调度中心统计列表")
    @ApiOperation(value="数据-调度中心统计列表", notes="调度中心统计列表")
    @RequestMapping(value = "/getCenterListData", method = RequestMethod.GET)
    public Result getCenterListData(@ApiParam(value = "开始时间")String beginTime,
                                   @ApiParam(value = "结束时间")String endTime,
                                   @ApiParam(value = "时间类型")int timeType){
        List<ActCenterData> dataList = actBusinessService.getCenterListData(beginTime,endTime,timeType);
        IPage<ActCenterData> page = new Page<>();
        page.setRecords(dataList);
        page.setTotal(dataList.size());
        return Result.ok(page);
    }



    @AutoLog(value = "数据-回访组统计列表")
    @ApiOperation(value="数据-回访组统计列表", notes="回访组统计列表")
    @RequestMapping(value = "/getVisitListData", method = RequestMethod.GET)
    public Result getVisitListData(@ApiParam(value = "开始时间")String beginTime,
                                   @ApiParam(value = "结束时间")String endTime,
                                   @ApiParam(value = "时间类型")int timeType){
        List<ActVisitData> dataList = actBusinessService.getVisitData(beginTime,endTime,timeType);
        IPage<ActVisitData> page = new Page<>();
        page.setRecords(dataList);
        page.setTotal(dataList.size());
        return Result.ok(page);
    }

    @AutoLog(value = "数据-待回访总量")
    @ApiOperation(value="数据-待回访总量", notes="待回访总量")
    @RequestMapping(value = "/findNoVisitNum", method = RequestMethod.GET)
    public Result findNoVisitNum(@ApiParam(value = "开始时间")String beginTime,
                                   @ApiParam(value = "结束时间")String endTime,
                                   @ApiParam(value = "时间类型")int timeType){
        int total = actBusinessService.findNoVisitNum(beginTime, endTime, timeType);
        return Result.ok(total);
    }

    @AutoLog(value = "数据-回访满意度统计")
    @ApiOperation(value="数据-回访满意度统计", notes="回访满意度统计")
    @RequestMapping(value = "/getVisitRankData", method = RequestMethod.GET)
    public Result getVisitRankData(@ApiParam(value = "开始时间")String beginTime,
                                   @ApiParam(value = "结束时间")String endTime,
                                   @ApiParam(value = "时间类型")int timeType){
        Map<String,int[]> dataList = actBusinessService.getVisitRankData(beginTime,endTime,timeType);

        return Result.ok(dataList);
    }


    @AutoLog(value = "数据-工单优先度数据")
    @ApiOperation(value="数据-工单优先度数据", notes="工单状态列表")
    @RequestMapping(value = "/getPriorityData", method = RequestMethod.GET)
    public Result getPriorityData(){

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getUsername();
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().or().taskCandidateUser(userId).
                taskAssignee(userId).endOr();
        Map<String,Object> dataMap = new HashMap<>();

        //普通
        long normalNum = query.taskPriority(0).count();

        dataMap.put("normalNum",normalNum);
        //重要
        long importantNum = query.taskPriority(1).count();
        dataMap.put("importantNum",importantNum);
        //紧急
        long criticalNum = query.taskPriority(2).count();
        dataMap.put("criticalNum",criticalNum);

        return Result.ok(dataMap);
    }

    @AutoLog(value = "数据-工单优先度数据")
    @ApiOperation(value="数据-工单优先度数据", notes="工单状态列表")
    @RequestMapping(value = "/getPriorityDataByBigScreen", method = RequestMethod.GET)
    public Result getPriorityDataByBigScreen(){

        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery();
        Map<String,Object> dataMap = new HashMap<>();
        //普通
        long normalNum = query.taskPriority(0).count();

        dataMap.put("normalNum",normalNum);
        //重要
        long importantNum = query.taskPriority(1).count();
        dataMap.put("importantNum",importantNum);
        //紧急
        long criticalNum = query.taskPriority(2).count();
        dataMap.put("criticalNum",criticalNum);

        return Result.ok(dataMap);
    }

    @AutoLog(value = "数据-工单各类型完成量数据")
    @ApiOperation(value="数据-工单各类型完成量数据", notes="工单各类型完成量数据")
    @RequestMapping(value = "/getBusinessStatusByBigScreen", method = RequestMethod.GET)
    public Result getBusinessStatusByBigScreen(){


        Map<String,Object> dataMap = actBusinessService.getBusinessStatusList();

        return Result.ok(dataMap);
    }

    @AutoLog(value = "数据-个人操作数据查询")
    @ApiOperation(value="数据-个人操作数据查询", notes="个人操作数据查询")
    @RequestMapping(value = "/getBusinessOperationQuery", method = RequestMethod.GET)
        public Result getBusinessOperationQuery(@ApiParam(value = "开始时间")String beginTime,
                                            @ApiParam(value = "结束时间")String endTime,
                                            @ApiParam(value = "流程名称")String name,
                                            @ApiParam(value = "当前页")String pageNo,
                                            @ApiParam(value = "当前页码")String pageSize,
                                            @ApiParam(value = "操作类型")Integer operaType){


        IPage<ActUserQueryData> page = actBusinessService.getBusinessOperationQuery(name,beginTime,endTime,pageNo,pageSize,operaType);
        return Result.ok(page);
    }

    @AutoLog(value = "数据-最近7日每日新增工单")
    @ApiOperation(value="数据-最近7日每日新增工单", notes="最近7日每日新增工单")
    @RequestMapping(value = "/getNewBusinessByBigScreen", method = RequestMethod.GET)
    public Result getNewBusinessByBigScreen(){


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 7);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        Map<String,Object> dataMap = new HashMap<>();
        Map<String,Object> query = new HashMap<>();
        query.put("afterTime",result);
        List<ActChartVo> vos = actBusinessService.findNewBusinessNum(query);

        List<ActChartVo> vos2 = actBusinessService.findNoHandleBusinessNum(query);

        dataMap.put("newOrder",vos);
        dataMap.put("notHandleOrder",vos2);
        return Result.ok(dataMap);
    }


    public static String parse_yyyyMMdd(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
        return format.format(time);
    }


}

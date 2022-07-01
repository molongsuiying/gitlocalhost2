package org.jeecg.modules.activiti.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.jeecg.modules.activiti.data.*;
import org.jeecg.modules.activiti.entity.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 流程业务扩展表
 * @Author: pmc
 * @Date:   2020-03-30
 * @Version: V1.0
 */
public interface ActBusinessMapper extends BaseMapper<ActBusiness> {

    @Select("select * from ${tableName} where id = #{tableId}")
    Map<String,Object> getBusiData(@Param("tableId") String tableId, @Param("tableName") String tableName);
    @Select("select * from ${tableName} where id in (${tableIdsSql})")
    List<Map<String,Object>> getBusiDatas(@Param("tableIdsSql") String tableIdsSql, @Param("tableName") String tableName);

    @Select("update ${tableName} set status = #{status} where id = #{tableId}")
    void updateFormStatus( @Param("tableName") String tableName,@Param("tableId") String tableId,@Param("status")Integer status);


    List<Map<String,Object>> getBusiDataList(@Param("tableName") String tableName,@Param("categoryId")String categoryId,@Param("status")String status,
                                             @Param("name")String name,@Param("mobile")String mobile,@Param("pageNo")String pageNo,@Param("pageSize")String pageSize);


    //@Select("select count(*) from ${tableName} where category_id = #{categoryId}")
    int countBusiDataList(@Param("tableName") String tableName,@Param("categoryId")String categoryId, @Param("status") String status,@Param("name")String name,@Param("mobile")String mobile);

    @Select("select count(id) from ${tableName} where id = #{tableId} and name = #{name} and mobile = #{mobile}")
    int checkedUserForm(@Param("tableName") String tableName,@Param("tableId") String tableId,@Param("name")String name,@Param("mobile")String mobile);

    @Insert("${sql}")
    int insertBusiData(@Param("sql") String sql);

    @Update("${sql}")
    int updateBusiData(@Param("sql") String sql);

    @Delete("delete from ${tableName} where id = #{tableId}")
    int deleteBusiData(@Param("tableId") String tableId, @Param("tableName") String tableName);

    @Select("SELECT Top 1 ahi.USER_ID_ FROM ACT_HI_IDENTITYLINK ahi\n" +
            "      WHERE TYPE_ = #{type} AND TASK_ID_ = #{taskId}\n")
    List<String> findUserIdByTypeAndTaskId(@Param("type") String type, @Param("taskId") String taskId);

    @Select("SELECT ahi.USER_ID_ FROM ACT_HI_IDENTITYLINK ahi\n" +
            "      WHERE TYPE_ = #{type} AND TASK_ID_ = #{taskId}\n")
    List<String> findUserIdListByTypeAndTaskId(@Param("type") String type, @Param("taskId") String taskId);

    @Insert("INSERT INTO ACT_HI_IDENTITYLINK (ID_, TYPE_, USER_ID_, TASK_ID_, PROC_INST_ID_)\n" +
            "      VALUES (#{id}, #{type}, #{userId}, #{taskId}, #{procInstId})")
    int insertHI_IDENTITYLINK(@Param("id") String id, @Param("type")String type, @Param("userId")String userId, @Param("taskId")String taskId, @Param("procInstId")String procInstId);


    @Insert("INSERT INTO ACT_HI_TASKINST (ID_, PROC_DEF_ID_, TASK_DEF_KEY_, PROC_INST_ID_, EXECUTION_ID_, NAME_, START_TIME_, END_TIME_, DELETE_REASON_)\n" +
            "      VALUES (#{id}, #{procDefId}, #{taskDefKey}, #{procInstId}, #{exId},#{name},#{startTime},#{endTime},#{reason})")
    void insertHI_TASKINST(@Param("id") String id, @Param("procDefId")String procDefId, @Param("taskDefKey")String taskDefKey,
                           @Param("procInstId")String procInstId, @Param("exId")String exId, @Param("name")String name,
                           @Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("reason")String reason);

    @Insert("INSERT INTO ACT_HI_COMMENT (ID_, TYPE_, TIME_, USER_ID_, TASK_ID_, PROC_INST_ID_, ACTION_, MESSAGE_, FULL_MSG_)\n" +
            "      VALUES (#{id}, #{type}, #{time}, #{userId}, #{taskId}, #{procInstId}, #{action}, #{message}, #{fullMessage})")
    void insertHI_COMMENT(@Param("id") String id,@Param("type") String type, @Param("time") Date time,@Param("userId") String userId,
                          @Param("taskId") String taskId, @Param("procInstId") String procInstId, @Param("action") String action, @Param("message") String message, @Param("fullMessage") byte[] fullMessage);

    @Select("SELECT ari.ID_ FROM ACT_RU_IDENTITYLINK ari\n" +
            "      WHERE TYPE_ = #{type} AND TASK_ID_ = #{taskId}")
    List<String> selectIRunIdentity(@Param("taskId")String taskId,@Param("type") String type);

    @Update("update ${tableName} set act_status = #{actStatus} where id = #{tableId}")
    int updateBusinessStatus(@Param("tableName")String tableName, @Param("tableId")String tableId, @Param("actStatus")String actStatus);

    @Select("select id from act_z_business where proc_def_id in " +
            "(select id from act_z_process where type_id in" +
            "(select id from sys_category where code like '${type}%')" +
            ")")
    List<String> listByTypeApp(@Param("type")String type);

    @Select(
            "select id from act_z_process where type_id in" +
            "(select id from sys_category where code like '${type}%')")
    List<String> proc_def_idListByType(@Param("type")String type);
    @Select(
            "select deployment_id from act_z_process where type_id in" +
            "(select id from sys_category where code like '${type}%')")
    List<String> deployment_idListByType(@Param("type")String type);

    void saveBusinessExtra(ActBusinessExtra actBusinessExtra);

    void updateBusinessExtra(ActBusinessExtra actBusinessExtra);

    ActBusinessExtra findBusinessExtraByBusinessId(@Param("businessId")String businessId, @Param("procSort")Integer procSort);

    //ActBusinessExtra findBusinessExtraByBusinessIdAndTaskKey(@Param("businessId")String businessId, @Param("taskKey")String taskKey);
    ActBusinessExtra findExtraByBusinessIdAndTaskKeyAndDepartCode(@Param("businessId")String businessId, @Param("taskKey")String taskKey,@Param("departCode")String departCode);

    Integer findMaxSortByBusinessId(@Param("businessId")String businessId);

    void saveBusinessPro(ActBusinessPro actBusinessPro);

    void updateBusinessPro(String businessId);

    void saveBusinessVisit(ActBusinessVisit actBusinessVisit);

    List<ActBusinessVisit> getVisitList(@Param("procInstId")String procInstId);

    void updateLatestVisit(Long id);

    List<ProcDataVo> selectProcDataTable(Map<String,Object> map);

    int findTotalBusinessNum();

    int findReNumByBusinessId(@Param("businessId")String businessId);

    int findDueByBusinessId(@Param("businessId")String businessId);

    List<ActChartVo> findMyBusinessIdNum(Map<String,Object> map);

    List<ActChartVo> findNewBusinessNum(Map<String,Object> map);

    List<ActChartVo> findNoHandleBusinessNum(Map<String,Object> map);

    //及时接收
    void saveTaskAway(ActHiTaskAway actHiTaskAway);

    //督办记录表
    void saveTaskSupervise(ActHiTaskSupervise supervise);

    List<ActHiTaskAway> findTaskAway(@Param("businessId")String businessId, @Param("taskId")String taskId,@Param("userId")String userId);

    //被督办次数
    int findSuperviseByDepart(Map<String,Object> map);
    //该部门处理工单量
    int findHandleNumByDepart(Map<String,Object> map);

    //该部门及时接受工单量
    int findAwayNumByDepart(Map<String,Object> map);

    //申请延期工单量
    int findPostponeNumByDepart(Map<String,Object> map);

    //逾期量
    int findDueNumByDepart(Map<String,Object> map);

    //重办工单量
    int findReNumByDepart(Map<String,Object> map);

    //退回量
    int findRefuseNumByDepart(Map<String,Object> map);

    //平均处理时长
    int findAvgHandleTimeByDepart(Map<String,Object> map);

    //平均接收时长
    int findAvgAwayTimeByDepart(Map<String,Object> map);

    //待接收工单
    int findTodoNumByDepart(Map<String,Object> map);

    //正常单
    int findNormalNumByDepart(Map<String,Object> map);

    //下派单
    int findUnderNumByDepart(Map<String,Object> map);

    //回访统计
    List<ActVisitData> findVisitData(Map<String,Object> map);

    int findReVisitNumByUser(Map<String,Object> map);

    int findNoVisitNum(Map<String,Object> map);

    List<ActChartVo> findAvgTimeByUser(Map<String,Object> map);

    List<ActRankData> findVisitRank(Map<String,Object> map);


    //调度中心
    List<ActCenterData> findDoneNumByCenterGroupUser(Map<String,Object> map);

    int findTodoNumByCenter(Map<String,Object> map);

    int findRefuseNumByCenter(Map<String,Object> map);

    int findRefusedNumByCenter(Map<String,Object> map);

    int findPassNumByCenter(Map<String,Object> map);

    ActChartVo findAvgTimeByCenter(Map<String,Object> map);

    //申请人的申请列表
    List<ActApplyUserFormVo> findApplyUserFormVo();

    //工单查询

    List<ActUserQueryData> findBusinessOperationByUser(Map<String,Object> map);
    long findTotalBusinessOperationByUser(Map<String,Object> map);

    //查询工单状态
    List<ActChartVo> findBusinessStatusList();
}

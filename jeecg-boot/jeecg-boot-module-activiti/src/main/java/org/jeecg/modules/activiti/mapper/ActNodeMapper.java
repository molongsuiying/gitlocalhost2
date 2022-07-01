package org.jeecg.modules.activiti.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.activiti.entity.*;

import java.util.HashMap;
import java.util.List;

/**
 * @Description: 流程节点扩展表
 * @Author: jeecg-boot
 * @Date:   2020-03-30
 * @Version: V1.0
 */
public interface ActNodeMapper extends BaseMapper<ActNode> {

    List<LoginUser> findUserByNodeId(@Param("nodeId") String nodeId);

    List<Role> findRoleByNodeId(String nodeId);

    List<Role> findAppointRoleByNodeId(String nodeId);


    List<LoginUser> findUserByRoleIdsAndDepartIds(HashMap<String,Object> hashMap);

    List<Department> findDepartmentByNodeId(String nodeId);

    List<Role> findRolesByCodes(@Param("roleCodes") List roleCodes);

    List<Department> findDepartmentsByIds(@Param("departIds")List departIds);

    List<Department> findAppointDepartmentByNodeId(String nodeId);
    @Select("select role_code from sys_role where id in (select role_id from sys_user_role where user_id = (select id from sys_user where username=#{username}))")
    List<String> getRoleByUserName(@Param("username") String username);
    @Select("select * from sys_user")
    List<LoginUser> queryAllUser();
    @Select("select * from sys_user where id in (select user_id from sys_user_role where role_id = #{id})")
    List<LoginUser> findUserByRoleId(@Param("id") String id);
    @Select("select * from sys_user where id in (select user_id from sys_user_depart where dep_id = #{id})")
    List<LoginUser> findUserDepartmentId(@Param("id") String id);
    @Select("select * from act_z_node where node_id = #{nodeId}")
    List<ActNode> findByNodeId(@Param("nodeId") String nodeId);

    ActNodeUnite findByNodeIdAndProDefId(@Param("nodeId")String nodeId,@Param("defId")String defId);

    void saveUnite(ActNodeUnite unite);

    void updateUnite(ActNodeUnite unite);

    ActTaskUnite findTaskUnite(@Param("taskId")String taskId,@Param("procDefId")String procDefId);

    void saveTaskUnite(ActTaskUnite taskUnite);

    String findRelateIdByNodeId(@Param("nodeId")String nodeId);
}

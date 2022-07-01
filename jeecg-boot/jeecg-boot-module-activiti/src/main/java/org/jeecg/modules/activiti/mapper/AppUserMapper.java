package org.jeecg.modules.activiti.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.activiti.entity.AppUser;
import org.jeecg.modules.activiti.entity.AppUserPermission;
import org.jeecg.modules.activiti.entity.AppUserRole;
import org.jeecg.modules.activiti.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUserMapper extends BaseMapper<AppUser> {

//    @Select("select * from sys_user where username = #{username,jdbcType=VARCHAR}")
    List<AppUserRole> getUserId(String id);

    List<AppUserPermission> getSysId(String sid);

    @Select("select role_name from sys_role where id= #{id,jdbcType=VARCHAR}")
    Role getRoleName(String id);

}

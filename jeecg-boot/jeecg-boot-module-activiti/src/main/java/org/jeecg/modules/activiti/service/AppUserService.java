package org.jeecg.modules.activiti.service;

import org.jeecg.modules.activiti.entity.AppUser;
import org.jeecg.modules.activiti.entity.AppUserPermission;
import org.jeecg.modules.activiti.entity.AppUserRole;
import org.jeecg.modules.activiti.entity.Role;

import java.util.List;

public interface AppUserService {
    List<AppUserRole> getUserId(String id);
    List<AppUserPermission> getSysId(String sid);
    Role getRoleName(String id);
}

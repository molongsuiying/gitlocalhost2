package org.jeecg.modules.activiti.service.Impl;

import org.jeecg.modules.activiti.entity.AppUser;
import org.jeecg.modules.activiti.entity.AppUserPermission;
import org.jeecg.modules.activiti.entity.AppUserRole;
import org.jeecg.modules.activiti.entity.Role;
import org.jeecg.modules.activiti.mapper.AppUserMapper;
import org.jeecg.modules.activiti.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserServiceImpl implements AppUserService {
    @Autowired
    private AppUserMapper appUserMapper;
    @Override
    public List<AppUserRole> getUserId(String id) {
        List<AppUserRole> appUser = appUserMapper.getUserId(id);
//        System.out.println(appUser.getSysType()+"========================="+"Mapper层");
        return appUser;
    }

    @Override
    public List<AppUserPermission> getSysId(String sid) {
        List<AppUserPermission> appUser = appUserMapper.getSysId(sid);
        return appUser;
    }

    @Override
    public Role getRoleName(String id) {
        return appUserMapper.getRoleName(id);
    }
}

package org.jeecg.modules.activiti.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.ImportExcelUtil;
import org.jeecg.modules.activiti.entity.Role;
import org.jeecg.modules.activiti.mapper.AppUserMapper;
import org.jeecg.modules.activiti.mapper.AppRoleMapper;
import org.jeecg.modules.activiti.service.RoleService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
@Service
public class RoleServiceImpl extends ServiceImpl<AppRoleMapper, Role> implements RoleService {
    @Autowired
    AppRoleMapper AppRoleMapper;
    @Autowired
    AppUserMapper sysUserMapper;

    @Override
    public Result importExcelCheckRoleCode(MultipartFile file, ImportParams params) throws Exception {
        List<Object> listRoles = ExcelImportUtil.importExcel(file.getInputStream(), Role.class, params);
        int totalCount = listRoles.size();
        List<String> errorStrs = new ArrayList<>();

        // 去除 listRoles 中重复的数据
        for (int i = 0; i < listRoles.size(); i++) {
            String roleCodeI =((Role)listRoles.get(i)).getRoleCode();
            for (int j = i + 1; j < listRoles.size(); j++) {
                String roleCodeJ =((Role)listRoles.get(j)).getRoleCode();
                // 发现重复数据
                if (roleCodeI.equals(roleCodeJ)) {
                    errorStrs.add("第 " + (j + 1) + " 行的 roleCode 值：" + roleCodeI + " 已存在，忽略导入");
                    listRoles.remove(j);
                    break;
                }
            }
        }
        // 去掉 sql 中的重复数据
        Integer errorLines=0;
        Integer successLines=0;
        List<String> list = ImportExcelUtil.importDateSave(listRoles, RoleService.class, errorStrs, CommonConstant.SQL_INDEX_UNIQ_SYS_ROLE_CODE);
         errorLines+=list.size();
         successLines+=(listRoles.size()-errorLines);
        return ImportExcelUtil.imporReturnRes(errorLines,successLines,list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(String roleid) {
        //1.删除角色和用户关系
        AppRoleMapper.deleteRoleUserRelation(roleid);
        //2.删除角色和权限关系
        AppRoleMapper.deleteRolePermissionRelation(roleid);
        //3.删除角色
        this.removeById(roleid);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchRole(String[] roleIds) {
        //1.删除角色和用户关系
//        sysUserMapper.deleteBathRoleUserRelation(roleIds);
//        //2.删除角色和权限关系
//        sysUserMapper.deleteBathRolePermissionRelation(roleIds);
        //3.删除角色
        this.removeByIds(Arrays.asList(roleIds));
        return true;
    }

    @Override
    public Role getNameById(String id) {
        return AppRoleMapper.getNameById(id);
    }
}

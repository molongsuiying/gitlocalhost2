package org.jeecg.modules.activiti.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.activiti.entity.AppUserPermission;

/**
 * <p>
 * 用户权限表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface AppUserPermissionService extends IService<AppUserPermission> {
	
	/**
	 * 保存授权/先删后增
	 * @param roleId
	 * @param permissionIds
	 */
	public void saveUserPermission(String roleId,String permissionIds);
	
	/**
	 * 保存授权 将上次的权限和这次作比较 差异处理提高效率 
	 * @param roleId
	 * @param permissionIds
	 * @param lastPermissionIds
	 */
	public void saveUserPermission(String roleId,String permissionIds,String lastPermissionIds);

}

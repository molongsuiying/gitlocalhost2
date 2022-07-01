package org.jeecg.modules.activiti.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.activiti.entity.AppPermission;
import org.jeecg.modules.activiti.entity.AppSystem;
import org.jeecg.modules.activiti.entity.Role;
import org.jeecg.modules.activiti.model.TreeModel;

import java.util.List;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface AppPermissionService extends IService<AppPermission> {
	
	List<TreeModel> queryListByParentId(String parentId);
	
	/**真实删除*/
	public  void deletePermission(String id) throws JeecgBootException;
	/**逻辑删除*/
	public void deletePermissionLogical(String id) throws JeecgBootException;
	
	public void addPermission(AppPermission sysPermission) throws JeecgBootException;
	
	public void editPermission(AppPermission sysPermission) throws JeecgBootException;
	public List<Role> queryAllSys();

//	public List<AppPermission> queryByUser(String username);
	
//	/**
//	 * 根据permissionId删除其关联的SysPermissionDataRule表中的数据
//	 *
//	 * @param id
//	 * @return
//	 */
//	public void deletePermRuleByPermId(String id);
	
	/**
	  * 查询出带有特殊符号的菜单地址的集合
	 * @return
	 */
	public List<String> queryPermissionUrlWithStar();

//	/**
//	 * 判断用户否拥有权限
//	 * @param username
//	 * @param sysPermission
//	 * @return
//	 */
//	public boolean hasPermission(String username, AppPermission appPermission);

//	/**
//	 * 根据用户和请求地址判断是否有此权限
//	 * @param username
//	 * @param url
//	 * @return
//	 */
//	public boolean hasPermission(String username, String url);
}

package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.college.entity.AppClause;
import org.jeecg.modules.college.model.TreeModel;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface AppClauseService extends IService<AppClause> {
	
	List<TreeModel> queryListByParentId(String parentId);
	
	/**真实删除*/
	public  void deletePermission(String id) throws JeecgBootException;
	/**逻辑删除*/
	public void deletePermissionLogical(String id) throws JeecgBootException;
	
	public void addPermission(AppClause sysPermission) throws JeecgBootException;
	
	public void editPermission(AppClause sysPermission) throws JeecgBootException;

	/**
	 * 查询所有条款类型数据
	 * @return
	 * @throws JeecgBootException
	 */
	List<AppClause> queryAllAppClause(Map<String,Object> params) throws  JeecgBootException;

	/**
	 * 查询所有条款内容（三级）
	 * @return
	 * @throws JeecgBootException
	 */
	List<AppClause> queryAllAppClauseThree(Map<String,Object> params) throws  JeecgBootException;

	long countList(Map<String,Object> params);

	long countListThree(Map<String,Object> params);
}

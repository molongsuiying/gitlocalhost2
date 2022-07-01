package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.college.entity.AppClause;
import org.jeecg.modules.college.mapper.AppClauseMapper;
import org.jeecg.modules.college.model.TreeModel;
import org.jeecg.modules.college.service.AppClauseService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Service
public class AppClauseServiceImpl extends ServiceImpl<AppClauseMapper, AppClause> implements AppClauseService {

	@Resource
	private AppClauseMapper appClauseMapper;

	
//	@Resource
//	private ISysPermissionDataRuleService permissionDataRuleService;

//	@Resource
//	private SysRolePermissionMapper sysRolePermissionMapper;

//	@Resource
//	private SysDepartPermissionMapper sysDepartPermissionMapper;

//	@Resource
//	private SysDepartRolePermissionMapper sysDepartRolePermissionMapper;

	@Override
	public List<TreeModel> queryListByParentId(String parentId) {
		return appClauseMapper.queryListByParentId(parentId);
	}

	/**
	  * 真实删除
	 */
	@Override
	@Transactional
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
	public  void deletePermission(String id) throws JeecgBootException {
		AppClause appPermission = this.getById(id);
		if(appPermission==null) {
			throw new JeecgBootException("未找到菜单信息");
		}
		String pid = appPermission.getParentId();
		if(oConvertUtils.isNotEmpty(pid)) {
			Integer count = this.count(new QueryWrapper<AppClause>().lambda().eq(AppClause::getParentId, pid));
			if(count==1) {
				//若父节点无其他子节点，则该父节点是叶子节点
				this.appClauseMapper.setMenuLeaf(pid, 1);
			}
		}
		appClauseMapper.deleteById(id);
		// 该节点可能是子节点但也可能是其它节点的父节点,所以需要级联删除
		this.removeChildrenBy(appPermission.getId());
//		//关联删除
//		Map map = new HashMap<>();
//		map.put("permission_id",id);
//		//删除数据规则
//		this.deletePermRuleByPermId(id);
//		//删除角色授权表
//		sysRolePermissionMapper.deleteByMap(map);
//		//删除部门权限表
//		sysDepartPermissionMapper.deleteByMap(map);
//		//删除部门角色授权
//		sysDepartRolePermissionMapper.deleteByMap(map);
	}
	
	/**
	 * 根据父id删除其关联的子节点数据
	 * 
	 * @return
	 */
	public void removeChildrenBy(String parentId) {
		LambdaQueryWrapper<AppClause> query = new LambdaQueryWrapper<>();
		// 封装查询条件parentId为主键,
		query.eq(AppClause::getParentId, parentId);
		// 查出该主键下的所有子级
		List<AppClause> permissionList = this.list(query);
		if (permissionList != null && permissionList.size() > 0) {
			String id = ""; // id
			Integer num = 0; // 查出的子级数量
			// 如果查出的集合不为空, 则先删除所有
			this.remove(query);
			// 再遍历刚才查出的集合, 根据每个对象,查找其是否仍有子级
			for (int i = 0, len = permissionList.size(); i < len; i++) {
//				id = permissionList.get(i).getId();
//				Map map = new HashMap<>();
//				map.put("permission_id",id);
//				//删除数据规则
//				this.deletePermRuleByPermId(id);
//				//删除角色授权表
//				sysRolePermissionMapper.deleteByMap(map);
//				//删除部门权限表
//				sysDepartPermissionMapper.deleteByMap(map);
//				//删除部门角色授权
//				sysDepartRolePermissionMapper.deleteByMap(map);
				num = this.count(new LambdaQueryWrapper<AppClause>().eq(AppClause::getParentId, id));
				// 如果有, 则递归
				if (num > 0) {
					this.removeChildrenBy(id);
				}
			}
		}
	}
	
	/**
	  * 逻辑删除
	 */
	@Override
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
	//@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true,condition="#sysPermission.menuType==2")
	public void deletePermissionLogical(String id) throws JeecgBootException {
		AppClause sysPermission = this.getById(id);
		if(sysPermission==null) {
			throw new JeecgBootException("未找到菜单信息");
		}
		String pid = sysPermission.getParentId();
		Integer count = this.count(new QueryWrapper<AppClause>().lambda().eq(AppClause::getParentId, pid));
		if(count==1) {
			//若父节点无其他子节点，则该父节点是叶子节点
			this.appClauseMapper.setMenuLeaf(pid, 1);
		}
		sysPermission.setDelFlag(1);
		this.updateById(sysPermission);
	}

	@Override
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
	public void addPermission(AppClause sysPermission) throws JeecgBootException {
		//----------------------------------------------------------------------
		//判断是否是一级菜单，是的话清空父菜单
		if(CommonConstant.MENU_TYPE_0.equals(sysPermission.getMenuType())) {
			sysPermission.setParentId(null);
		}
		//----------------------------------------------------------------------
		String pid = sysPermission.getParentId();
		if(oConvertUtils.isNotEmpty(pid)) {
			//设置父节点不为叶子节点
			this.appClauseMapper.setMenuLeaf(pid, 0);
		}
		sysPermission.setCreateTime(new Date());
		sysPermission.setDelFlag(0);
		sysPermission.setLeaf(true);
		this.save(sysPermission);
	}

	@Override
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
	public void editPermission(AppClause sysPermission) throws JeecgBootException {
		AppClause p = this.getById(sysPermission.getId());
		//TODO 该节点判断是否还有子节点
		if(p==null) {
			throw new JeecgBootException("未找到菜单信息");
		}else {
			sysPermission.setUpdateTime(new Date());
			//----------------------------------------------------------------------
			//Step1.判断是否是一级菜单，是的话清空父菜单ID
			if(CommonConstant.MENU_TYPE_0.equals(sysPermission.getMenuType())) {
				sysPermission.setParentId("");
			}
			//Step2.判断菜单下级是否有菜单，无则设置为叶子节点
			Integer count = this.count(new QueryWrapper<AppClause>().lambda().eq(AppClause::getParentId, sysPermission.getId()));
			if(count==0) {
				sysPermission.setLeaf(true);
			}
			//----------------------------------------------------------------------
			this.updateById(sysPermission);
			
			//如果当前菜单的父菜单变了，则需要修改新父菜单和老父菜单的，叶子节点状态
			String pid = sysPermission.getParentId();
			if((oConvertUtils.isNotEmpty(pid) && !pid.equals(p.getParentId())) || oConvertUtils.isEmpty(pid)&&oConvertUtils.isNotEmpty(p.getParentId())) {
				//a.设置新的父菜单不为叶子节点
				this.appClauseMapper.setMenuLeaf(pid, 0);
				//b.判断老的菜单下是否还有其他子菜单，没有的话则设置为叶子节点
				Integer cc = this.count(new QueryWrapper<AppClause>().lambda().eq(AppClause::getParentId, p.getParentId()));
				if(cc==0) {
					if(oConvertUtils.isNotEmpty(p.getParentId())) {
						this.appClauseMapper.setMenuLeaf(p.getParentId(), 1);
					}
				}
				
			}
		}
		
	}
	/**
	 * 查询所有条款类型数据
	 *
	 * @return
	 * @throws JeecgBootException
	 */
	@Override
	public List<AppClause> queryAllAppClause(Map<String,Object> params) throws JeecgBootException {

		return appClauseMapper.queryAppClauseList();

	}

	/**
	 * 查询所有条款内容（三级）
	 *
	 * @return
	 * @throws JeecgBootException
	 */
	@Override
	public List<AppClause> queryAllAppClauseThree(Map<String,Object> params) throws JeecgBootException {
		return appClauseMapper.queryThreeList();
	}

	@Override
	public long countList(Map<String, Object> params) {
		return appClauseMapper.countList(params);
	}

	@Override
	public long countListThree(Map<String, Object> params) {
		return appClauseMapper.countListThree(params);
	}
}

package org.jeecg.modules.college.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.MD5Util;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.college.entity.AppClause;
import org.jeecg.modules.college.model.AppClauseTree;
import org.jeecg.modules.college.service.AppClauseService;
import org.jeecg.modules.college.util.ClauseDataUtil;
import org.jeecg.modules.college.util.CollegeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p>
 * 申请类型表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Slf4j
@RestController
@RequestMapping("/app/clause")
public class AppClauseController {

	@Autowired
	private AppClauseService AppClauseService;
//	@Autowired
//	private AppUserPermissionService appUserPermissionService;

	/**
	 * 加载全部条款分类列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<AppClause>> list(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        long start = System.currentTimeMillis();
		Result<IPage<AppClause>> result = new Result<>();
		IPage<AppClause> page = new Page<>(pageNo, pageSize);
		Map<String,Object> query = CollegeUtil.getParameterMap(req);
		try{
			query.put("pageNo",pageNo);
			query.put("pageSize",pageSize);
			List<AppClause> pageList = AppClauseService.queryAllAppClause(query);
			log.info("获取的列表内容长度："+ pageList.size());
			long count = AppClauseService.countList(query);
			page.setRecords(pageList);
			page.setTotal(count);
			result.setResult(page);
			result.setSuccess(true);
			log.info("======获取全部类型数据=====耗时:" + (System.currentTimeMillis() - start) + "毫秒");
		}catch (Exception e){
			log.error(e.getMessage(), e);
		}
//		try {
//			LambdaQueryWrapper<AppClause> query = new LambdaQueryWrapper<AppClause>();
//			query.eq(AppClause::getDelFlag, CommonConstant.DEL_FLAG_0);
//			query.orderByAsc(AppClause::getSortNo);
//			List<AppClause> list = AppClauseService.list(query);
//			log.info("获取的列表内容长度："+ list.size());
//			List<AppClauseTree> treeList = new ArrayList<>();
//			getTreeList(treeList, list, null);
//			result.setResult(treeList);
//			result.setSuccess(true);
//            log.info("======获取全部类型数据=====耗时:" + (System.currentTimeMillis() - start) + "毫秒");
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
		return result;
	}

	/**
	 * 加载全部条款内容列表
	 *
	 * @return
	 */
	@RequestMapping(value = "/listThree", method = RequestMethod.GET)
	public Result<IPage<AppClause>> listThree(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
		long start = System.currentTimeMillis();
		Result<IPage<AppClause>> result = new Result<>();
		IPage<AppClause> page = new Page<>(pageNo, pageSize);
		Map<String,Object> query = CollegeUtil.getParameterMap(req);
		try{
			query.put("pageNo",pageNo);
			query.put("pageSize",pageSize);
			List<AppClause> pageList = AppClauseService.queryAllAppClauseThree(query);
			log.info("获取的列表内容长度："+ pageList.size());
			long count = AppClauseService.countListThree(query);
			page.setRecords(pageList);
			page.setTotal(count);
			result.setResult(page);
			result.setSuccess(true);
			log.info("======获取全部类型数据=====耗时:" + (System.currentTimeMillis() - start) + "毫秒");
		}catch (Exception e){
			log.error(e.getMessage(), e);
		}
		return result;
	}

	/*update_begin author:wuxianquan date:20190908 for:先查询一级菜单，当用户点击展开菜单时加载子菜单 */
	/**
	 * 条款列表(一级类型)
	 *
	 * @return
	 */
	@RequestMapping(value = "/getSystemMenuList", method = RequestMethod.GET)
	public Result<List<AppClauseTree>> getSystemMenuList() {
        long start = System.currentTimeMillis();
		Result<List<AppClauseTree>> result = new Result<>();
		try {
			LambdaQueryWrapper<AppClause> query = new LambdaQueryWrapper<AppClause>();
			query.eq(AppClause::getMenuType,CommonConstant.MENU_TYPE_0);
			query.eq(AppClause::getDelFlag, CommonConstant.DEL_FLAG_0);
			query.orderByAsc(AppClause::getSortNo);
			List<AppClause> list = AppClauseService.list(query);
			List<AppClauseTree> AppClauseTreeList = new ArrayList<AppClauseTree>();
			for(AppClause AppClause : list){
				AppClauseTree AppClauseTree = new AppClauseTree(AppClause);
				AppClauseTreeList.add(AppClauseTree);
			}
			result.setResult(AppClauseTreeList);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
        log.info("======获取一级菜单数据=====耗时:" + (System.currentTimeMillis() - start) + "毫秒");
		return result;
	}

	/**
	 * 查询子类型（单个）
	 * @param parentId
	 * @return
	 */
	@RequestMapping(value = "/getSystemSubmenu", method = RequestMethod.GET)
	public Result<List<AppClauseTree>> getSystemSubmenu(@RequestParam("parentId") String parentId){
		Result<List<AppClauseTree>> result = new Result<>();
		try{
			LambdaQueryWrapper<AppClause> query = new LambdaQueryWrapper<AppClause>();
			query.eq(AppClause::getParentId,parentId);
			query.eq(AppClause::getDelFlag, CommonConstant.DEL_FLAG_0);
			query.orderByAsc(AppClause::getSortNo);
			List<AppClause> list = AppClauseService.list(query);
			List<AppClauseTree> AppClauseTreeList = new ArrayList<AppClauseTree>();
			for(AppClause AppClause : list){
				AppClauseTree AppClauseTree = new AppClauseTree(AppClause);
				AppClauseTreeList.add(AppClauseTree);
			}
			result.setResult(AppClauseTreeList);
			result.setSuccess(true);
		}catch (Exception e){
			log.error(e.getMessage(), e);
		}
		return result;
	}
	/*update_end author:wuxianquan date:20190908 for:先查询一级菜单，当用户点击展开菜单时加载子菜单 */

	// update_begin author:sunjianlei date:20200108 for: 新增批量根据父ID查询子级菜单的接口 -------------
	/**
	 * 查询子类（多个父类）
	 *
	 * @param parentIds 父ID（多个采用半角逗号分割）
	 * @return 返回 key-value 的 Map
	 */
	@GetMapping("/getSystemSubmenuBatch")
	public Result getSystemSubmenuBatch(@RequestParam("parentIds") String parentIds) {
		try {
			LambdaQueryWrapper<AppClause> query = new LambdaQueryWrapper<>();
			List<String> parentIdList = Arrays.asList(parentIds.split(","));
			query.in(AppClause::getParentId, parentIdList);
			query.eq(AppClause::getDelFlag, CommonConstant.DEL_FLAG_0);
			query.orderByAsc(AppClause::getSortNo);
			List<AppClause> list = AppClauseService.list(query);
			Map<String, List<AppClauseTree>> listMap = new HashMap<>();
			for (AppClause item : list) {
				String pid = item.getParentId();
				if (parentIdList.contains(pid)) {
					List<AppClauseTree> mapList = listMap.get(pid);
					if (mapList == null) {
						mapList = new ArrayList<>();
					}
					mapList.add(new AppClauseTree(item));
					listMap.put(pid, mapList);
				}
			}
			return Result.ok(listMap);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Result.error("批量查询子菜单失败：" + e.getMessage());
		}
	}
	// update_end author:sunjianlei date:20200108 for: 新增批量根据父ID查询子级菜单的接口 -------------

	/**
	 * 添加条款
	 * @param permission
	 * @return
	 */
//	@RequiresRoles({ "admin" })
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Result<AppClause> add(@RequestBody AppClause permission) {
		Result<AppClause> result = new Result<AppClause>();
		try {
			permission = ClauseDataUtil.intelligentProcessData(permission);
			AppClauseService.addPermission(permission);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  * 编辑菜单
	 * @param permission
	 * @return
	 */
//	@RequiresRoles({ "admin" })
	@RequestMapping(value = "/edit", method = { RequestMethod.PUT, RequestMethod.POST })
	public Result<AppClause> edit(@RequestBody AppClause permission) {
		Result<AppClause> result = new Result<>();
		try {
			permission = ClauseDataUtil.intelligentProcessData(permission);
			AppClauseService.editPermission(permission);
			result.success("修改成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  * 删除菜单
	 * @param id
	 * @return
	 */
//	@RequiresRoles({ "admin" })
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<AppClause> delete(@RequestParam(name = "id", required = true) String id) {
		Result<AppClause> result = new Result<>();
		try {
			AppClauseService.deletePermission(id);
			result.success("删除成功!");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500(e.getMessage());
		}
		return result;
	}

	/**
	  * 批量删除菜单
	 * @param ids
	 * @return
	 */
	@RequiresRoles({ "admin" })
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<AppClause> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<AppClause> result = new Result<>();
		try {
            String[] arr = ids.split(",");
			for (String id : arr) {
				if (oConvertUtils.isNotEmpty(id)) {
					AppClauseService.deletePermission(id);
				}
			}
			result.success("删除成功!");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("删除成功!");
		}
		return result;
	}


	private void getTreeList(List<AppClauseTree> treeList, List<AppClause> metaList, AppClauseTree temp) {
		for (AppClause permission : metaList) {
			String tempPid = permission.getParentId();
			AppClauseTree tree = new AppClauseTree(permission);
			if (temp == null && oConvertUtils.isEmpty(tempPid)) {
				treeList.add(tree);
				if (!tree.getIsLeaf()) {
					getTreeList(treeList, metaList, tree);
				}
			} else if (temp != null && tempPid != null && tempPid.equals(temp.getId())) {
				temp.getChildren().add(tree);
				if (!tree.getIsLeaf()) {
					getTreeList(treeList, metaList, tree);
				}
			}

		}
	}

	/**
	  *  获取权限JSON数组
	 * @param jsonArray
	 * @param allList
	 */
	private void getAllAuthJsonArray(JSONArray jsonArray,List<AppClause> allList) {
		JSONObject json = null;
		for (AppClause permission : allList) {
			json = new JSONObject();
			json.put("status", permission.getStatus());
			json.put("describe", permission.getName());
			jsonArray.add(json);
		}
	}

	/**
	  *  获取权限JSON数组
	 * @param jsonArray
	 * @param metaList
	 */
	private void getAuthJsonArray(JSONArray jsonArray,List<AppClause> metaList) {
		for (AppClause permission : metaList) {
			if(permission.getMenuType()==null) {
				continue;
			}
			JSONObject json = null;
			if(permission.getMenuType().equals(CommonConstant.MENU_TYPE_2) &&CommonConstant.STATUS_1.equals(permission.getStatus())) {
				json = new JSONObject();
				json.put("describe", permission.getName());
				jsonArray.add(json);
			}
		}
	}
	/**
	  *  获取菜单JSON数组
	 * @param jsonArray
	 * @param metaList
	 * @param parentJson
	 */
	private void getPermissionJsonArray(JSONArray jsonArray, List<AppClause> metaList, JSONObject parentJson) {
		for (AppClause permission : metaList) {
			if (permission.getMenuType() == null) {
				continue;
			}
			String tempPid = permission.getParentId();
			JSONObject json = getPermissionJsonObject(permission);
			if(json==null) {
				continue;
			}
			if (parentJson == null && oConvertUtils.isEmpty(tempPid)) {
				jsonArray.add(json);
				if (!permission.isLeaf()) {
					getPermissionJsonArray(jsonArray, metaList, json);
				}
			} else if (parentJson != null && oConvertUtils.isNotEmpty(tempPid) && tempPid.equals(parentJson.getString("id"))) {
				// 类型( 0：一级菜单 1：子菜单 2：按钮 )
				if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_2)) {
					JSONObject metaJson = parentJson.getJSONObject("meta");
					if (metaJson.containsKey("permissionList")) {
						metaJson.getJSONArray("permissionList").add(json);
					} else {
						JSONArray permissionList = new JSONArray();
						permissionList.add(json);
						metaJson.put("permissionList", permissionList);
					}
					// 类型( 0：一级菜单 1：子菜单 2：按钮 )
				} else if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_1) || permission.getMenuType().equals(CommonConstant.MENU_TYPE_0)) {
					if (parentJson.containsKey("children")) {
						parentJson.getJSONArray("children").add(json);
					} else {
						JSONArray children = new JSONArray();
						children.add(json);
						parentJson.put("children", children);
					}

					if (!permission.isLeaf()) {
						getPermissionJsonArray(jsonArray, metaList, json);
					}
				}
			}

		}
	}

	/**
	 * 根据菜单配置生成路由json
	 * @param permission
	 * @return
	 */
		private JSONObject getPermissionJsonObject(AppClause permission) {
		JSONObject json = new JSONObject();
		// 类型(0：一级菜单 1：子菜单 2：按钮)
		if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_2)) {
			//json.put("action", permission.getPerms());
			//json.put("type", permission.getPermsType());
			//json.put("describe", permission.getName());
			return null;
		} else if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_0) || permission.getMenuType().equals(CommonConstant.MENU_TYPE_1)) {
			json.put("id", permission.getId());
			if (permission.isRoute()) {
				json.put("route", "1");// 表示生成路由
			} else {
				json.put("route", "0");// 表示不生成路由
			}

			if (isWWWHttpUrl(permission.getUrl())) {
				json.put("path", MD5Util.MD5Encode(permission.getUrl(), "utf-8"));
			} else {
				json.put("path", permission.getUrl());
			}

			// 重要规则：路由name (通过URL生成路由name,路由name供前端开发，页面跳转使用)


				json.put("name", urlToRouteName(permission.getUrl()));


			// 是否隐藏路由，默认都是显示的
			if (permission.isHidden()) {
				json.put("hidden", true);
			}
			// 聚合路由
			if (permission.isAlwaysShow()) {
				json.put("alwaysShow", true);
			}
//			json.put("component", permission.getComponent());
			JSONObject meta = new JSONObject();
			// 由用户设置是否缓存页面 用布尔值
			if (permission.isKeepAlive()) {
				meta.put("keepAlive", true);
			} else {
				meta.put("keepAlive", false);
			}

			/*update_begin author:wuxianquan date:20190908 for:往菜单信息里添加外链菜单打开方式 */
			//外链菜单打开方式
			if (permission.isInternalOrExternal()) {
				meta.put("internalOrExternal", true);
			} else {
				meta.put("internalOrExternal", false);
			}
			/* update_end author:wuxianquan date:20190908 for: 往菜单信息里添加外链菜单打开方式*/

			meta.put("title", permission.getName());
			if (oConvertUtils.isEmpty(permission.getParentId())) {
				// 一级菜单跳转地址
//				json.put("redirect", permission.getRedirect());
				if (oConvertUtils.isNotEmpty(permission.getIcon())) {
					meta.put("icon", permission.getIcon());
				}
			} else {
				if (oConvertUtils.isNotEmpty(permission.getIcon())) {
					meta.put("icon", permission.getIcon());
				}
			}
			if (isWWWHttpUrl(permission.getUrl())) {
				meta.put("url", permission.getUrl());
			}
			json.put("meta", meta);
		}

		return json;
	}

	/**
	 * 判断是否外网URL 例如： http://localhost:8080/jeecg-boot/swagger-ui.html#/ 支持特殊格式： {{
	 * window._CONFIG['domianURL'] }}/druid/ {{ JS代码片段 }}，前台解析会自动执行JS代码片段
	 * 
	 * @return
	 */
	private boolean isWWWHttpUrl(String url) {
		if (url != null && (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("{{"))) {
			return true;
		}
		return false;
	}

	/**
	 * 通过URL生成路由name（去掉URL前缀斜杠，替换内容中的斜杠‘/’为-） 举例： URL = /isystem/role RouteName =
	 * isystem-role
	 * 
	 * @return
	 */
	private String urlToRouteName(String url) {
		if (oConvertUtils.isNotEmpty(url)) {
			if (url.startsWith("/")) {
				url = url.substring(1);
			}
			url = url.replace("/", "-");

			// 特殊标记
			url = url.replace(":", "@");
			return url;
		} else {
			return null;
		}
	}
}

package org.jeecg.modules.activiti.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.activiti.entity.AppPermission;
import org.jeecg.modules.activiti.entity.AppSystem;
import org.jeecg.modules.activiti.entity.AppUserPermission;
import org.jeecg.modules.activiti.entity.Role;
import org.jeecg.modules.activiti.model.AppPermissionTree;
import org.jeecg.modules.activiti.service.AppSystemService;
import org.jeecg.modules.activiti.service.AppUserService;
import org.jeecg.modules.activiti.service.RoleService;
import org.jeecg.modules.activiti.util.PermissionDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("app/system")
public class AppSystemController {
    @Autowired
    private AppSystemService appSystemService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private RoleService roleService;


    @RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<AppSystem>> queryPageList(AppSystem role,
                                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                  HttpServletRequest req) {
		Result<IPage<AppSystem>> result = new Result<IPage<AppSystem>>();
		QueryWrapper<AppSystem> queryWrapper = QueryGenerator.initQueryWrapper(role, req.getParameterMap());
		queryWrapper.orderByAsc("sort_no");
		Page<AppSystem> page = new Page<AppSystem>(pageNo, pageSize);
		IPage<AppSystem> pageList = appSystemService.page(page, queryWrapper);
		pageList.getRecords().forEach(a->{
            List<AppUserPermission> list1 = appUserService.getSysId(a.getId());
            List<String> list = new ArrayList<String>();
            list.add("[");
            for(int i=0;i<list1.size();i++) {
                Role name = roleService.getNameById(list1.get(i).getPermissionId());
                list.add(name.getRoleName());
                if(i==list1.size()-1){
                    break;
                }
                list.add(",");
            }
            list.add("]");
            a.setRole(list);
        });
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

    @RequestMapping(value = "/add",method = RequestMethod.PUT)
    public Result<AppSystem> add(@RequestBody JSONObject jsonObject) {
        Result<AppSystem> result = new Result<AppSystem>();
        try {
            AppSystem appSystem = JSON.parseObject(jsonObject.toJSONString(), AppSystem.class);
            if(appSystemService.selectName().stream()
                    .filter(item->item.getName()
                            .equals(appSystem.getName()))
                    .findAny()
                    .isPresent())
            {
                result.error500("该名称已存在");
                return result;
            }
           if(appSystemService.selectName().stream()
                    .filter(item->item.getUrl()
                            .equals(appSystem.getUrl()))
                    .findAny()
                    .isPresent())
            {
                result.error500("Url已存在");
                return result;
            }
            appSystem.setStatus("0");
            appSystemService.addSystem(appSystem);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
//            result.error500("操作失败");
        }
        return result;
    }

    @RequestMapping(value = "/edit", method = { RequestMethod.POST })
    public Result<AppSystem> edit(@RequestBody JSONObject jsonObject) {
        Result<AppSystem> result = new Result<>();
        try {
//            permission = PermissionDataUtil.intelligentProcessData(permission);
            AppSystem appSystem = JSON.parseObject(jsonObject.toJSONString(), AppSystem.class);

            appSystem.setRole(null);
            if(appSystemService.selectNameAndUrl(appSystem.getId()).stream()
                    .filter(item->item.getName()
                            .equals(appSystem.getName()))
                    .findAny()
                    .isPresent())
            {
                result.error500("该名称已存在");
                return result;
            }
            if(appSystemService.selectNameAndUrl(appSystem.getId()).stream()
                    .filter(item->item.getUrl()
                            .equals(appSystem.getUrl()))
                    .findAny()
                    .isPresent())
            {
                result.error500("Url已存在");
                return result;
            }
            appSystemService.editSystem(appSystem);
            result.success("修改成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
//            result.error500("操作失败");
        }
        return result;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<AppSystem> delete(@RequestParam(name = "id", required = true) String id) {
        Result<AppSystem> result = new Result<>();
        try {
            appSystemService.deleteSystem(id);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

//    @RequiresRoles({ "admin" })
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<AppSystem> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<AppSystem> result = new Result<>();
        try {
            String[] arr = ids.split(",");
            for (String id : arr) {
                if (oConvertUtils.isNotEmpty(id)) {
                    appSystemService.deleteSystem(id);
                }
            }
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除成功!");
        }
        return result;
    }
}

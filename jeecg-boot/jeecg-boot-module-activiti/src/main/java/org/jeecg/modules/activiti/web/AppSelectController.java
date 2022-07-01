package org.jeecg.modules.activiti.web;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.expr.instruct.ForEach;
import org.jeecg.common.util.GetBeanUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.activiti.entity.AppUser;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.activiti.entity.AppUserPermission;
import org.jeecg.modules.activiti.entity.AppUserRole;
import org.jeecg.modules.activiti.entity.Role;
import org.jeecg.modules.activiti.service.AppSystemService;
import org.jeecg.modules.activiti.service.AppUserService;
import org.jeecg.modules.activiti.service.SqlTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/app")
@Api(tags="系统选择")
@Slf4j
public class AppSelectController {
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private AppSystemService appSystemService;
    @Autowired
    private SqlTableService sqlTableService;
    @RequestMapping(value = "select1",method = RequestMethod.PUT)
    public  Result<AppUser>  select1(@RequestBody JSONObject jsonObject){
        Result<AppUser> result = new Result<AppUser>();
            String ids = jsonObject.getString("id");
            String sysId = jsonObject.getString("sysType");
        System.out.println(sysId+"=================");
            List<AppUserRole> list= appUserService.getUserId(ids);
            List<AppUserPermission> list1 = appUserService.getSysId(sysId);
            if(list1.isEmpty()){
                result.error500("抱歉您目前没有资格进入该系统");
            }else {
                System.out.println(list.get(0)+"==========="+list1.size());
                c:for (AppUserRole a:list) {
                    for (AppUserPermission b:list1) {
                        if(a.getRoleId().equals(b.getPermissionId())){
                            result.success("欢迎进入本系统!");
                            break c;
                        }else {
                            result.error500("抱歉您目前没有资格进入该系统");
                        }
                        System.out.println(b.getId());
                    }
                    System.out.println(a.getId());
                }
            }

        return result;
    }
//    @RequestMapping("/select2")
//    public Result select2(@RequestParam String id){
//        Result<String> result = new Result<>();
//        List<String> listName = new ArrayList<String>();
//        List<AppUserPermission> list1 = appUserService.getSysId(id);
//        for (AppUserPermission a:list1) {
//            Role list2=appUserService.getRoleName(a.getPermissionId());
//            listName.add(list2.getRoleName());
//        }
//        return result;
//    }
    @RequestMapping("/select2")
    public Result result2(){
        Result<List<String>> result = new Result<>();
        List<String> list = sqlTableService.getTable().stream().sorted().collect(Collectors.toList());
        for(int i=0;i<list.size();i++){
            log.info(list.get(i));
        }

        return result;
    }


}

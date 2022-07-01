package org.jeecg.modules.activiti.web;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.activiti.entity.ActApplyUserFormVo;
import org.jeecg.modules.activiti.entity.ActBusiness;
import org.jeecg.modules.activiti.entity.ActZproComplain;
import org.jeecg.modules.activiti.service.IActZprocessService;
import org.jeecg.modules.activiti.service.Impl.ActBusinessServiceImpl;
import org.jeecg.modules.activiti.service.Impl.ActNodeServiceImpl;
import org.jeecg.modules.activiti.service.Impl.ActZproComplainServiceImpl;
import org.jeecg.modules.activiti.service.Impl.ActZprocessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@RestController
@RequestMapping("/pro_complain")
@Slf4j
@Transactional
@Api(tags="投诉")
public class ActComplainController {

    @Autowired
    private ActZproComplainServiceImpl actZproComplainService;
    @Autowired
    private ActNodeServiceImpl actNodeService;
    @Autowired
    private ActBusinessServiceImpl actBusinessService;

    @AutoLog(value = "获取可用模板")
    @ApiOperation(value="投诉-获取可用模板", notes="获取可用模板")
    @RequestMapping(value = "/listData" ,method = RequestMethod.GET)
    public Result listData(@ApiParam(value = "名称" )String mc,
                           @ApiParam(value = "是否最新" )Boolean zx,
                           @ApiParam(value = "状态 部署后默认1激活" )String status,
                           @ApiParam(value = "如果此项不为空，则会过滤当前用户的角色权限" )Boolean roles,
                           @ApiParam(value = "业务表名")String tableName){
        log.info("-------------流程列表-------------");
        LambdaQueryWrapper<ActZproComplain> wrapper = new LambdaQueryWrapper<ActZproComplain>();
        wrapper.orderByAsc(ActZproComplain::getSort).orderByDesc(ActZproComplain::getVersion);
        if (StrUtil.isNotBlank(mc)){
            wrapper.like(ActZproComplain::getName, mc);
        }

        if (zx!=null&&zx){
            wrapper.eq(ActZproComplain::getLatest, 1);
        }
        if (StrUtil.isNotBlank(status)){
            wrapper.eq(ActZproComplain::getStatus, status);
        }
        if(StringUtils.isNotBlank(tableName)){
            wrapper.eq(ActZproComplain::getBusinessTable, tableName);
        }

        List<ActZproComplain> list = actZproComplainService.list(wrapper);

        if (roles!=null&&roles){ //过滤角色
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            List<String> roleByUserName = actNodeService.getRoleByUserName(sysUser.getUsername());
            list = list.stream().filter(p->{
                String pRoles = p.getRoles();
                if (StrUtil.isBlank(pRoles)) {
                    return true; //未设置授权的所有人都能用
                }else {
                    String[] split = pRoles.split(",");
                    for (String role : split) {
                        if (roleByUserName.contains(role)){
                            return true;
                        }
                    }
                }
                return false;
            }).collect(Collectors.toList());

        }
        return Result.ok(list);
    }

    @AutoLog(value = "获取可用模板-外部使用")
    @ApiOperation(value="投诉-获取可用模板-外部使用", notes="获取可用模板-外部使用")
    @RequestMapping(value = "/listDataByOut" ,method = RequestMethod.GET)
    public Result listDataByOut(@ApiParam(value = "名称" )String mc,
                           @ApiParam(value = "是否最新" )Boolean zx,
                           @ApiParam(value = "状态 部署后默认1激活" )String status,
                           @ApiParam(value = "如果此项不为空，则会过滤当前用户的角色权限" )Boolean roles,
                           @ApiParam(value = "业务表名")String tableName){
        log.info("-------------流程列表-------------");
        LambdaQueryWrapper<ActZproComplain> wrapper = new LambdaQueryWrapper<ActZproComplain>();
        wrapper.orderByAsc(ActZproComplain::getSort).orderByDesc(ActZproComplain::getVersion);
        if (StrUtil.isNotBlank(mc)){
            wrapper.like(ActZproComplain::getName, mc);
        }

        if (zx!=null&&zx){
            wrapper.eq(ActZproComplain::getLatest, 1);
        }
        if (StrUtil.isNotBlank(status)){
            wrapper.eq(ActZproComplain::getStatus, status);
        }
        if(StringUtils.isNotBlank(tableName)){
            wrapper.eq(ActZproComplain::getBusinessTable, tableName);
        }

        List<ActZproComplain> list = actZproComplainService.list(wrapper);

        if (roles!=null&&roles){ //过滤角色
            list = list.stream().filter(p->{
                String pRoles = p.getRoles();
                if (StrUtil.isBlank(pRoles)) {
                    return true; //未设置授权的所有人都能用
                }
                return false;
            }).collect(Collectors.toList());

        }
        return Result.ok(list);
    }

    @RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
    public Result<Object> updateInfo(@RequestBody ActZproComplain actZproComplain){
        if(StringUtils.isNotBlank(actZproComplain.getId())){



            actZproComplainService.updateById(actZproComplain);
        }
        return Result.ok("修改成功");
    }

    @RequestMapping(value = "/buildInfo", method = RequestMethod.POST)
    public Result<Object> buildInfo(@RequestBody ActZproComplain actZproComplain){

        if(StringUtils.isBlank(actZproComplain.getId())){
            actZproComplain.setUpdateTime(new Date());
            actZproComplainService.save(actZproComplain);
        }
        return Result.ok("保存成功");
    }


    @RequestMapping(value = "/updateStatus")
    public Result updateStatus(@RequestBody JSONObject jsonObject){
        String id = jsonObject.getString("id");
        int status = jsonObject.getInteger("status");
        ActZproComplain actZproComplain = actZproComplainService.getById(id);
        actZproComplain.setStatus(status);
        actZproComplainService.updateById(actZproComplain);
        return Result.ok("修改成功！");
    }


    @AutoLog(value = "投诉-提交投诉申请")
    @ApiOperation(value="投诉-提交投诉申请", notes="业务表单参数数据一并传过来！")
    @PostMapping(value = "/add")
    public Result add(HttpServletRequest request){
        /*保存业务表单数据到数据库表*/
        String tableId = IdUtil.simpleUUID();
        //如果前端上传了id
        String id = request.getParameter("id");
        if( id != null && !id.equals("")){
            tableId = id;
        }
        actBusinessService.saveApplyForm(tableId, request);
        return Result.ok("提交成功");
    }

    @AutoLog(value = "投诉-提交投诉申请-外部")
    @ApiOperation(value="投诉-提交投诉申请-外部", notes="业务表单参数数据一并传过来！")
    @PostMapping(value = "/addByOut")
    public Result addByOut(@RequestBody JSONObject jsonObject){
        /*保存业务表单数据到数据库表*/
        String tableId = IdUtil.simpleUUID();
        //如果前端上传了id
        String id = jsonObject.getString("id");
        if( id != null && !id.equals("")){
            tableId = id;
        }
        actBusinessService.saveApplyFormByOut(tableId, jsonObject);
        return Result.ok("提交成功");
    }



    /*获取业务表单信息*/
    @AutoLog(value = "投诉-获取业务表单信息")
    @ApiOperation(value="投诉-获取业务表单信息", notes="获取业务表单信息")
    @RequestMapping(value = "/getForm", method = RequestMethod.GET)
    public Result getForm(@ApiParam(value = "业务表数据id" ,required = true)String tableId,
                          @ApiParam(value = "业务表名" ,required = true)String tableName){
        if (StrUtil.isBlank(tableName)){
            return Result.error("参数缺省！");
        }
        Map<String, Object> applyForm = actBusinessService.getApplyForm(tableId, tableName);
        return Result.ok(applyForm);
    }


    /*获取业务表单信息*/
    @AutoLog(value = "投诉-获取业务表单信息列表")
    @ApiOperation(value="投诉-获取业务表单信息列表", notes="获取业务表单信息")
    @RequestMapping(value = "/getFormList", method = RequestMethod.GET)
    public Result getFormList(@ApiParam(value = "业务表名" ,required = true)String tableName,
                              @ApiParam(value = "投诉类型" ,required = true)String categoryId,
                              @ApiParam(value = "状态" ,required = true)String status,
                              @ApiParam(value = "姓名" ,required = true)String name,
                              @ApiParam(value = "手机号" ,required = true)String mobile,
                              @ApiParam(value = "pageNo" ,required = true)String pageNo,
                              @ApiParam(value = "pageSize" ,required = true)String pageSize){
        if (StrUtil.isBlank(tableName)){
            return Result.error("参数缺省！");
        }
        List<Map<String, Object>> applyFormList;

        if(StringUtils.isBlank(pageNo)){
            pageNo = "0";
        }
        if(StringUtils.isBlank(pageSize)){
            pageNo = "10";
        }

        applyFormList = actBusinessService.getApplyFormList(tableName,categoryId,status,name,mobile,pageNo,pageSize);
        return Result.ok(applyFormList);
    }

    /*获取业务表单信息*/
    @AutoLog(value = "投诉-获取业务表单信息列表总和")
    @ApiOperation(value="投诉-获取业务表单信息列表总和", notes="获取业务表单信息")
    @RequestMapping(value = "/countFormList", method = RequestMethod.GET)
    public Result countFormList(@ApiParam(value = "业务表名" ,required = true)String tableName,
                                @ApiParam(value = "状态" ,required = true)String status,
                                @ApiParam(value = "姓名" ,required = true)String name,
                                @ApiParam(value = "手机号" ,required = true)String mobile,
                              @ApiParam(value = "投诉类型" ,required = true)String categoryId){
        if (StrUtil.isBlank(tableName)){
            return Result.error("参数缺省！");
        }

        int count  = actBusinessService.countApplyFormList(tableName,categoryId,status,name,mobile);
        return Result.ok(count);
    }


    /*修改业务表单信息*/
    @AutoLog(value = "投诉-修改业务表单信息")
    @ApiOperation(value="投诉-修改业务表单信息", notes="业务表单参数数据一并传过来!")
    @RequestMapping(value = "/editForm", method = RequestMethod.POST)
    public Result editForm(@ApiParam(value = "业务表数据id" ,required = true) @RequestParam("id") String id,
                           HttpServletRequest request){
        /*保存业务表单数据到数据库表*/
        actBusinessService.saveApplyForm(id,request);
        return Result.ok();
    }

    @AutoLog(value = "流程-投诉人回访说明")
    @ApiOperation(value="流程-投诉人回访说明", notes="投诉人回访说明")
    @RequestMapping(value = "/addComplainRemark", method = RequestMethod.POST)
    public Result<Object> addComplainRemark(@ApiParam(value = "数据表ID" ,required = true) @RequestParam String tableId,
                                            @ApiParam(value = "数据表名称" ,required = true) @RequestParam String tableName,
                                            @ApiParam(value = "回访说明" ,required = false) @RequestParam(required = false) String reason,
                                            @ApiParam(value = "回访满意度" ,required = false) @RequestParam(required = false) String degree){
        if(StrUtil.isBlank(reason)){
            reason = "";
        }
        //修改业务表的流程字段
        if(StringUtils.isBlank(degree)){
            degree = "4";
        }
        ActBusiness actBusiness = actBusinessService.getOneBean(new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getTableId,tableId));
        actBusiness.setComplainFlag(1);
        actBusiness.setComplainTime(new Date());
        actBusiness.setComplainRemark(reason);
        actBusiness.setComplainRank(degree);
        actBusinessService.update(actBusiness,new LambdaQueryWrapper<ActBusiness>().eq(ActBusiness::getTableId,tableId));
        return Result.ok("操作成功");
    }


    @AutoLog(value = "投诉-投诉人所有表单")
    @ApiOperation(value="投诉-投诉人所有表单", notes="投诉人所有表单")
    @RequestMapping(value = "/getApplyUserFormList", method = RequestMethod.GET)
    public Result getApplyUserFormList(@Param("name")String name,@Param("mobile")String mobile){
        if (StrUtil.isBlank(name)|| StringUtils.isBlank(mobile)){
            return Result.error("请正确输入姓名+手机号 如:李白+13888888888!");
        }

        List<ActApplyUserFormVo> list = actBusinessService.findApplyUserFormVo(name,mobile);

        return Result.ok(list);
    }
}

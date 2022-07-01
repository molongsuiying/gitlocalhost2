package org.jeecg.modules.college.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.college.entity.HonourFile;
import org.jeecg.modules.college.entity.HonourProject;
import org.jeecg.modules.college.service.HonourProjectService;
import org.jeecg.modules.college.util.CollegeUtil;
import org.jeecg.modules.college.util.ExcelUtil;
import org.jeecg.modules.college.vo.HonourVo;
import org.jeecg.modules.college.vo.ProjectVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@RestController
@RequestMapping("/honour/project")
@Slf4j
public class HonourProjectController {

    @Resource
    private HonourProjectService projectService;

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Resource
    private ISysBaseAPI sysBaseAPI;


    /**
     * 证书类
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryHonourProjectList", method = RequestMethod.GET)
    public Result<IPage<HonourProject>> queryHonourProjectList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<HonourProject>> result = new Result<>();
        IPage<HonourProject> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);

        query = sysBaseAPI.getQueryMap(query);

        List<HonourProject> pageList = projectService.queryList(query);
        long count = projectService.countList(query);
        page.setRecords(pageList);
        page.setTotal(count);
        result.setResult(page);
        result.setSuccess(true);
        return result;
    }


    /**
     *   添加
     * @param
     * @return
     */
    @RequestMapping(value = "/saveProject", method = RequestMethod.POST)
    public Result<HonourProject> saveProject(@RequestBody HonourProject project) {
        Result<HonourProject> result = new Result<>();

        try {


            if(project.getStatus()== null || project.getStatus() < CommonConstant.HONOUR_EXAMINE_ING){
                project.setStatus(CommonConstant.HONOUR_NO_SUBMIT);
            }

            project.setOpenShow(CommonConstant.DEL_FLAG_0);
            projectService.save(project);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     *  提交审核
     * @param
     * @return
     */
    @RequestMapping(value = "/submitProject", method = RequestMethod.POST)
    public Result<HonourProject> submitProject(@RequestBody HonourProject project) {
        Result<HonourProject> result = new Result<>();


        HonourProject old = projectService.getById(project.getId());

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        try {

            if (!sysUser.getUsername().equals(old.getCreateBy())){
                return result.error500("权限不足");
            }

            project.setStatus(CommonConstant.HONOUR_EXAMINE_ING);
            projectService.updateById(project);
            result.success("提交审核成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     *  批量提交审核
     * @param
     * @return
     */
    @RequestMapping(value = "/batchSubmit", method = RequestMethod.DELETE)
    public Result<HonourProject> batchSubmit(@RequestParam(name = "ids", required = true) String ids) {
        Result<HonourProject> result = new Result<>();

        List<String> agreeIds = Arrays.asList(ids.split(","));

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        try {

            for (int i = 0; i < agreeIds.size(); i++) {
                HonourProject old = projectService.getById(agreeIds.get(i));
                if (!sysUser.getUsername().equals(old.getCreateBy())){
                    return result.error500("权限不足");
                }
                if(old.getStatus() != null && old.getStatus() < 1){
                    old.setStatus(CommonConstant.HONOUR_EXAMINE_ING);
                    projectService.updateById(old);
                }

            }

            result.success("批量提交成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }


    /**
     *  编辑
     * @param project
     * @return
     */
    @PutMapping(value = "/editProject")
    public Result<HonourProject> editProject(@RequestBody HonourProject project) {
        Result<HonourProject> result = new Result<>();
        HonourProject old = projectService.getById(project.getId());

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();



        Boolean adminFlag = sysBaseAPI.checkAdmin(sysUser.getUsername());
        try {
            if (!adminFlag){
                if(old.getStatus().equals(CommonConstant.HONOUR_EXAMINE_ING)){
                    return result.error500("无法编辑已过审的荣誉");
                }

                if(!old.getCreateBy().equals(sysUser.getUsername())){
                    return result.error500("权限不足!无法编辑");
                }
            }else{
                if(CommonConstant.HONOUR_SUCCESS.equals(old.getStatus())){
                    project.setStatus(old.getStatus());
                }
            }
            projectService.updateById(project);
            result.success("编辑成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("编辑失败");
        }

        return result;
    }


    /**
     *  通过审核
     * @param
     * @return
     */
    @RequestMapping(value = "/pass", method = RequestMethod.POST)
    public Result<HonourProject> pass(@RequestBody HonourProject entity) {
        Result<HonourProject> result = new Result<>();


        HonourProject old = projectService.getById(entity.getId());

        try {
            //需要权限
            if(old.getStatus() != null && old.getStatus().equals(CommonConstant.HONOUR_EXAMINE_ING)){
                old.setStatus(CommonConstant.HONOUR_SUCCESS);
                old.setRemarks(entity.getRemarks());
                projectService.updateById(old);
            }
            result.success("审核成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     *  驳回审核
     * @param
     * @return
     */
    @RequestMapping(value = "/refuse", method = RequestMethod.POST)
    public Result<HonourProject> refuse(@RequestBody HonourProject entity) {
        Result<HonourProject> result = new Result<>();


        HonourProject old = projectService.getById(entity.getId());


        try {
            //需要权限
            if(!CommonConstant.HONOUR_SUCCESS.equals(old.getStatus())){
                old.setStatus(CommonConstant.HONOUR_REJECT);
                old.setRemarks(entity.getRemarks());
                projectService.updateById(old);
            }
            result.success("驳回成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     *  导出EXCEL
     * @param
     * @return
     */
    @RequestMapping(value = "/exportXls", method = RequestMethod.GET)
    public Result<String> exportXls(@RequestParam(name="customizeCells") String customizeCells,
                                    @RequestParam(name="biz",required = false) String biz,HttpServletRequest req) {
        Result<String> result = new Result<>();

        if(StringUtils.isBlank(biz)){
            biz = "excel";
        }
        Map<String,Object> query = CollegeUtil.getParameterMap(req);

        List<HonourProject> pageList = new ArrayList<>();

        String ids = req.getParameter("ids");
        if(StringUtils.isNotBlank(ids)){
            List<String> personIds = Arrays.asList(ids.split(","));
            pageList = projectService.queryListByIds(personIds);
        }else{
            query = sysBaseAPI.getQueryMap(query);
            pageList = projectService.queryList(query);
        }

        List<ProjectVo> vos = projectService.transVo(pageList);
        try {
            List<String> cells = Arrays.asList(customizeCells.split(","));
            Map<String, Object> fieldsName = new HashMap<>();

            for (int i = 0; i < cells.size(); i++) {
                fieldsName.put(cells.get(i),null);
            }
            ExcelUtil<ProjectVo> util = new ExcelUtil<>(ProjectVo.class);
            Format format = new SimpleDateFormat("yyMMdd");
            String time = format.format(new Date());

            String fileName = "课题类荣誉表"+"-" + time + ".xlsx";
            String ctxPath = uploadpath+ File.separator+biz + File.separator + time +File.separator+ fileName;
            util.exportExcel(vos, fieldsName,"课题类荣誉",ctxPath);

            String savePath = biz + File.separator + time + File.separator  + fileName;
            if (savePath.contains("\\")) {
                savePath = savePath.replace("\\", "/");
            }
            result.success("导出EXCEL成功！");
            result.setResult(savePath);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("导出EXCEL失败");
        }
        return result;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response)throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        Result<List<ProjectVo>> result = new Result<>();

        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            try {
                InputStream in = file.getInputStream();
                List<List<Object>> listob = ExcelUtil.getListByExcel(in,file.getOriginalFilename());
                List<ProjectVo> vos = projectService.transVoByExcel(listob);
                log.info(listob.toString());
                result.setSuccess(true);
                result.setResult(vos);
                return result;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result.error500(e.getMessage());
                }
            }
        }
        return result;
    }


    /**
     *  excel保存
     * @param
     * @return
     */
    @RequestMapping(value = "/saveListByExcel", method = RequestMethod.POST)
    public Result<HonourProject> saveListByExcel(@RequestBody List<ProjectVo> vos) {
        Result<HonourProject> result = new Result<>();

        try {
            projectService.saveListByExcel(vos);
            result.setMessage("保存成功");
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     *  附件保存
     * @param
     * @return
     */
    @RequestMapping(value = "/saveAppendixList", method = RequestMethod.POST)
    public Result<HonourFile> saveAppendixList(@RequestBody List<HonourVo> vos) {
        Result<HonourFile> result = new Result<>();

        try {
            projectService.saveAppendixList(vos);
            result.setMessage("保存成功");
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "通过id删除")
    @ApiOperation(value = "通过id删除", notes = "通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {


            HonourProject entity = projectService.getById(id);
            if(CommonConstant.HONOUR_NO_SUBMIT.equals(entity.getStatus())||CommonConstant.HONOUR_REJECT.equals(entity.getStatus())){
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                List<String> roleCodes = sysBaseAPI.getRolesByUsername(sysUser.getUsername());
                if(entity.getCreateBy().equals(sysUser.getUsername())||roleCodes.contains("admin")){
                    projectService.removeById(id);
                }
            }

        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }
}

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
import org.jeecg.modules.college.entity.AppAccident;
import org.jeecg.modules.college.service.AppAccidentService;
import org.jeecg.modules.college.util.CollegeUtil;
import org.jeecg.modules.college.util.ExcelUtil;
import org.jeecg.modules.college.vo.HonourVo;
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
@RequestMapping("/app/accident")
@Slf4j
public class AppAccidentController {

    @Resource
    private AppAccidentService fileService;

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Resource
    private ISysBaseAPI sysBaseAPI;


    /**
     * 人才类
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryAppAccidentList", method = RequestMethod.GET)
    public Result<IPage<AppAccident>> queryAppAccidentList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                         @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<AppAccident>> result = new Result<>();
        IPage<AppAccident> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);


        query = sysBaseAPI.getQueryMap(query);

        List<AppAccident> pageList = fileService.queryList(query);
        long count = fileService.countList(query);
        page.setRecords(pageList);
        page.setTotal(count);
        result.setResult(page);
        result.setSuccess(true);
        return result;
    }


    @RequestMapping(value = "/queryAppAccidentListByCreateBy", method = RequestMethod.GET)
    public Result<IPage<AppAccident>> queryAppAccidentListByCreateBy(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                         @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<AppAccident>> result = new Result<>();
        IPage<AppAccident> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);
//        query.put();


        query = sysBaseAPI.getQueryMap(query);

        List<AppAccident> pageList = fileService.queryList(query);

        long count = fileService.countList(query);
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
    @RequestMapping(value = "/saveFile", method = RequestMethod.POST)
    public Result<AppAccident> saveFile(@RequestBody AppAccident file) {
        Result<AppAccident> result = new Result<>();
        try {
            if(file.getStatus() == null || file.getStatus() < CommonConstant.HONOUR_EXAMINE_ING){
                file.setStatus(CommonConstant.HONOUR_NO_SUBMIT);
            }
            file.setOpenShow(CommonConstant.DEL_FLAG_0);
            fileService.save(file);
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
    @RequestMapping(value = "/submitFile", method = RequestMethod.POST)
    public Result<AppAccident> submitFile(@RequestBody AppAccident file) {
        Result<AppAccident> result = new Result<>();


        AppAccident old = fileService.getById(file.getId());

//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        try {

//            if (!sysUser.getUsername().equals(old.getCreateBy())){
//                return result.error500("权限不足");
//            }

            old.setStatus(CommonConstant.HONOUR_EXAMINE_ING);
            fileService.updateById(old);
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
    public Result<AppAccident> batchSubmit(@RequestParam(name = "ids", required = true) String ids) {
        Result<AppAccident> result = new Result<>();

        List<String> agreeIds = Arrays.asList(ids.split(","));

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        try {

            for (int i = 0; i < agreeIds.size(); i++) {
                AppAccident old = fileService.getById(agreeIds.get(i));
                if (!sysUser.getUsername().equals(old.getCreateBy())){
                    return result.error500("权限不足");
                }
                if(old.getStatus() != null && old.getStatus() < 1){
                    old.setStatus(CommonConstant.HONOUR_EXAMINE_ING);
                    fileService.updateById(old);
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
     * @param file
     * @return
     */
    @PutMapping(value = "/editFile")
    public Result<AppAccident> editFile(@RequestBody AppAccident file) {
        Result<AppAccident> result = new Result<>();
        AppAccident old = fileService.getById(file.getId());

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        Boolean adminFlag = sysBaseAPI.checkAdmin(sysUser.getUsername());

        try {
            if (adminFlag){
                if(CommonConstant.HONOUR_SUCCESS.equals(old.getStatus())){
                    file.setStatus(old.getStatus());
                }
                fileService.updateById(file);
                result.success("编辑成功！");
            }else{
                if(old.getStatus().equals(CommonConstant.HONOUR_EXAMINE_ING)){
                    return result.error500("无法编辑已过审的荣誉");
                }

                if(!old.getCreateBy().equals(sysUser.getUsername())){
                    return result.error500("权限不足!无法编辑");
                }
                fileService.updateById(file);
                result.success("编辑成功！");
            }

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
    public Result<AppAccident> pass(@RequestBody AppAccident entity) {
        Result<AppAccident> result = new Result<>();


        AppAccident old = fileService.getById(entity.getId());

        try {
            //需要权限
            if(old.getStatus() != null && old.getStatus().equals(CommonConstant.HONOUR_EXAMINE_ING)){
                old.setStatus(CommonConstant.HONOUR_SUCCESS);
                old.setRemarks(entity.getRemarks());
                fileService.updateById(old);
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
    public Result<AppAccident> refuse(@RequestBody AppAccident entity) {
        Result<AppAccident> result = new Result<>();


        AppAccident old = fileService.getById(entity.getId());

//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        try {
            //需要权限
//            if(!CommonConstant.HONOUR_SUCCESS.equals(old.getStatus())){
//                old.setStatus(CommonConstant.HONOUR_REJECT);
//                old.setRemarks(entity.getRemarks());
//                fileService.updateById(old);
//            }
            old.setStatus(CommonConstant.HONOUR_REJECT);
            old.setRemarks(entity.getRemarks());
            fileService.updateById(old);
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



        List<AppAccident> pageList = new ArrayList<>();

        String ids = req.getParameter("ids");
        if(StringUtils.isNotBlank(ids)){
            List<String> personIds = Arrays.asList(ids.split(","));
            pageList = fileService.queryListByIds(personIds);
        }else{
            query = sysBaseAPI.getQueryMap(query);
            pageList = fileService.queryList(query);
        }

//        List<FileVo> vos = fileService.transVo(pageList);
        try {
            List<String> cells = Arrays.asList(customizeCells.split(","));
            Map<String, Object> fieldsName = new HashMap<>();

            for (int i = 0; i < cells.size(); i++) {
                fieldsName.put(cells.get(i),null);
            }
            ExcelUtil<AppAccident> util = new ExcelUtil<>(AppAccident.class);
            Format format = new SimpleDateFormat("yyMMdd");

            String time = format.format(new Date());

            String fileName = "文件类荣誉表"+"-" + time + ".xlsx";
            String ctxPath = uploadpath+ File.separator+biz + File.separator + time +File.separator+ fileName;
            util.exportExcel(pageList, fieldsName,"文件类荣誉",ctxPath);

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
        Result<List<List<Object>>> result = new Result<>();

        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            try {
                InputStream in = file.getInputStream();
                List<List<Object>> listob = ExcelUtil.getListByExcel(in,file.getOriginalFilename());
//                List<FileVo> fileVos = fileService.transVoByExcel(listob);
                log.info(listob.toString());
                result.setSuccess(true);
                result.setResult(listob);
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
    public Result<AppAccident> saveListByExcel(@RequestBody List<AppAccident> vos) {
        Result<AppAccident> result = new Result<>();

        try {
//            fileService.saveListByExcel(vos);
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
    public Result<AppAccident> saveAppendixList(@RequestBody List<HonourVo> vos) {
        Result<AppAccident> result = new Result<>();

        try {
//            fileService.saveAppendixList(vos);
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


            AppAccident file = fileService.getById(id);
//            if(CommonConstant.HONOUR_NO_SUBMIT.equals(file.getStatus())||CommonConstant.HONOUR_REJECT.equals(file.getStatus())){
//                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//                List<String> roleCodes = sysBaseAPI.getRolesByUsername(sysUser.getUsername());
//                if(file.getCreateBy().equals(sysUser.getUsername())||roleCodes.contains("admin")){
//                    fileService.removeById(id);
//                }
//            }
            fileService.removeById(id);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

}

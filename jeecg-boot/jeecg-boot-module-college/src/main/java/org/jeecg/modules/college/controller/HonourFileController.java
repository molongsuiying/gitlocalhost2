package org.jeecg.modules.college.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.ImportExcelUtil;
import org.jeecg.modules.college.entity.Honour;
import org.jeecg.modules.college.entity.HonourAgree;
import org.jeecg.modules.college.entity.HonourFile;
import org.jeecg.modules.college.service.HonourFileService;
import org.jeecg.modules.college.util.CollegeUtil;
import org.jeecg.modules.college.util.ExcelUtil;
import org.jeecg.modules.college.vo.AgreeVo;
import org.jeecg.modules.college.vo.FileVo;
import org.jeecg.modules.college.vo.HonourVo;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
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
@RequestMapping("/honour/file")
@Slf4j
public class HonourFileController {

    @Resource
    private HonourFileService fileService;

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Resource
    private ISysBaseAPI sysBaseAPI;


    /**
     * ?????????
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryHonourFileList", method = RequestMethod.GET)
    public Result<IPage<HonourFile>> queryHonourFileList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                         @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<HonourFile>> result = new Result<>();
        IPage<HonourFile> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);


        query = sysBaseAPI.getQueryMap(query);

        List<HonourFile> pageList = fileService.queryList(query);
        long count = fileService.countList(query);
        page.setRecords(pageList);
        page.setTotal(count);
        result.setResult(page);
        result.setSuccess(true);
        return result;
    }

    @RequestMapping(value = "/queryHonourFile", method = RequestMethod.GET)
    public Result<HonourFile> queryHonourFile(@RequestParam("id") String id) {
//        String id = honourFile.getId();
        Result<HonourFile> result = new Result<>();
        HonourFile pageList = fileService.queryList(id);
        result.setResult(pageList);
        result.setSuccess(true);
        return result;
    }

    @RequestMapping(value = "/queryHonourFileListByCreateBy", method = RequestMethod.GET)
    public Result<IPage<HonourFile>> queryHonourFileListByCreateBy(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                         @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<HonourFile>> result = new Result<>();
        IPage<HonourFile> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);
//        query.put();


        query = sysBaseAPI.getQueryMap(query);

        List<HonourFile> pageList = fileService.queryList(query);

        long count = fileService.countList(query);
        page.setRecords(pageList);
        page.setTotal(count);
        result.setResult(page);
        result.setSuccess(true);
        return result;
    }


    /**
     *   ??????
     * @param
     * @return
     */
    @RequestMapping(value = "/saveFile", method = RequestMethod.POST)
    public Result<HonourFile> saveFile(@RequestBody HonourFile file) {
        Result<HonourFile> result = new Result<>();

        try {

            if(file.getStatus() == null || file.getStatus() < CommonConstant.HONOUR_EXAMINE_ING){
                file.setStatus(CommonConstant.HONOUR_NO_SUBMIT);
            }

            file.setOpenShow(CommonConstant.DEL_FLAG_0);

            fileService.save(file);
            result.success("???????????????");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("????????????");
        }
        return result;
    }

    /**
     *  ????????????
     * @param
     * @return
     */
    @RequestMapping(value = "/submitFile", method = RequestMethod.POST)
    public Result<HonourFile> submitFile(@RequestBody HonourFile file) {
        Result<HonourFile> result = new Result<>();


        HonourFile old = fileService.getById(file.getId());

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        try {

            if (!sysUser.getUsername().equals(old.getCreateBy())){
                return result.error500("????????????");
            }

            file.setStatus(CommonConstant.HONOUR_EXAMINE_ING);
            fileService.updateById(file);
            result.success("?????????????????????");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("????????????");
        }
        return result;
    }

    /**
     *  ??????????????????
     * @param
     * @return
     */
    @RequestMapping(value = "/batchSubmit", method = RequestMethod.DELETE)
    public Result<HonourFile> batchSubmit(@RequestParam(name = "ids", required = true) String ids) {
        Result<HonourFile> result = new Result<>();

        List<String> agreeIds = Arrays.asList(ids.split(","));

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        try {

            for (int i = 0; i < agreeIds.size(); i++) {
                HonourFile old = fileService.getById(agreeIds.get(i));
                if (!sysUser.getUsername().equals(old.getCreateBy())){
                    return result.error500("????????????");
                }
                if(old.getStatus() != null && old.getStatus() < 1){
                    old.setStatus(CommonConstant.HONOUR_EXAMINE_ING);
                    fileService.updateById(old);
                }

            }

            result.success("?????????????????????");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("????????????");
        }
        return result;
    }

    /**
     *  ??????
     * @param file
     * @return
     */
    @PutMapping(value = "/editFile")
    public Result<HonourFile> editFile(@RequestBody HonourFile file) {
        Result<HonourFile> result = new Result<>();
        HonourFile old = fileService.getById(file.getId());

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        Boolean adminFlag = sysBaseAPI.checkAdmin(sysUser.getUsername());

        try {
            if (adminFlag){
                if(CommonConstant.HONOUR_SUCCESS.equals(old.getStatus())){
                    file.setStatus(old.getStatus());
                }
                fileService.updateById(file);
                result.success("???????????????");
            }else{
                if(old.getStatus().equals(CommonConstant.HONOUR_EXAMINE_ING)){
                    return result.error500("??????????????????????????????");
                }

                if(!old.getCreateBy().equals(sysUser.getUsername())){
                    return result.error500("????????????!????????????");
                }
                fileService.updateById(file);
                result.success("???????????????");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("????????????");
        }


        return result;
    }

    /**
     *  ????????????
     * @param
     * @return
     */
    @RequestMapping(value = "/pass", method = RequestMethod.POST)
    public Result<HonourFile> pass(@RequestBody HonourFile entity) {
        Result<HonourFile> result = new Result<>();


        HonourFile old = fileService.getById(entity.getId());

        try {
            //????????????
            if(old.getStatus() != null && old.getStatus().equals(CommonConstant.HONOUR_EXAMINE_ING)){
                old.setStatus(CommonConstant.HONOUR_SUCCESS);
                old.setRemarks(entity.getRemarks());
                fileService.updateById(old);
            }
            result.success("???????????????");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("????????????");
        }
        return result;
    }

    /**
     *  ????????????
     * @param
     * @return
     */
    @RequestMapping(value = "/refuse", method = RequestMethod.POST)
    public Result<HonourFile> refuse(@RequestBody HonourFile entity) {
        Result<HonourFile> result = new Result<>();


        HonourFile old = fileService.getById(entity.getId());

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        try {
            //????????????
            if(!CommonConstant.HONOUR_SUCCESS.equals(old.getStatus())){
                old.setStatus(CommonConstant.HONOUR_REJECT);
                old.setRemarks(entity.getRemarks());
                fileService.updateById(old);
            }
            result.success("???????????????");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("????????????");
        }
        return result;
    }

    /**
     *  ??????EXCEL
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



        List<HonourFile> pageList = new ArrayList<>();

        String ids = req.getParameter("ids");
        if(StringUtils.isNotBlank(ids)){
            List<String> personIds = Arrays.asList(ids.split(","));
            pageList = fileService.queryListByIds(personIds);
        }else{
            query = sysBaseAPI.getQueryMap(query);
            pageList = fileService.queryList(query);
        }

        List<FileVo> vos = fileService.transVo(pageList);
        try {
            List<String> cells = Arrays.asList(customizeCells.split(","));
            Map<String, Object> fieldsName = new HashMap<>();

            for (int i = 0; i < cells.size(); i++) {
                fieldsName.put(cells.get(i),null);
            }
            ExcelUtil<FileVo> util = new ExcelUtil<>(FileVo.class);
            Format format = new SimpleDateFormat("yyMMdd");

            String time = format.format(new Date());

            String fileName = "??????????????????"+"-" + time + ".xlsx";
            String ctxPath = uploadpath+ File.separator+biz + File.separator + time +File.separator+ fileName;
            util.exportExcel(vos, fieldsName,"???????????????",ctxPath);

            String savePath = biz + File.separator + time + File.separator  + fileName;
            if (savePath.contains("\\")) {
                savePath = savePath.replace("\\", "/");
            }
            result.success("??????EXCEL?????????");
            result.setResult(savePath);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("??????EXCEL??????");
        }
        return result;
    }

    /**
     * ??????excel????????????
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response)throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        Result<List<FileVo>> result = new Result<>();

        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// ????????????????????????
            try {
                InputStream in = file.getInputStream();
                List<List<Object>> listob = ExcelUtil.getListByExcel(in,file.getOriginalFilename());
                List<FileVo> fileVos = fileService.transVoByExcel(listob);
                log.info(listob.toString());
                result.setSuccess(true);
                result.setResult(fileVos);
                return result;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("??????????????????:" + e.getMessage());
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
     *  excel??????
     * @param
     * @return
     */
    @RequestMapping(value = "/saveListByExcel", method = RequestMethod.POST)
    public Result<HonourFile> saveListByExcel(@RequestBody List<FileVo> vos) {
        Result<HonourFile> result = new Result<>();

        try {
            fileService.saveListByExcel(vos);
            result.setMessage("????????????");
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("????????????");
        }
        return result;
    }

    /**
     *  ????????????
     * @param
     * @return
     */
    @RequestMapping(value = "/saveAppendixList", method = RequestMethod.POST)
    public Result<HonourFile> saveAppendixList(@RequestBody List<HonourVo> vos) {
        Result<HonourFile> result = new Result<>();

        try {
            fileService.saveAppendixList(vos);
            result.setMessage("????????????");
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("????????????");
        }
        return result;
    }


    /**
     * ??????id??????
     *
     * @param id
     * @return
     */
    @AutoLog(value = "??????id??????")
    @ApiOperation(value = "??????id??????", notes = "??????id??????")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {


            HonourFile file = fileService.getById(id);
            if(CommonConstant.HONOUR_NO_SUBMIT.equals(file.getStatus())||CommonConstant.HONOUR_REJECT.equals(file.getStatus())){
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                List<String> roleCodes = sysBaseAPI.getRolesByUsername(sysUser.getUsername());
                if(file.getCreateBy().equals(sysUser.getUsername())||roleCodes.contains("admin")){
                    fileService.removeById(id);
                }
            }

        } catch (Exception e) {
            log.error("????????????", e.getMessage());
            return Result.error("????????????!");
        }
        return Result.ok("????????????!");
    }

    /*????????????????????????*/
    @AutoLog(value = "?????????-????????????????????????")
    @ApiOperation(value="?????????-????????????????????????", notes="????????????????????????")
    @RequestMapping(value = "/getForm", method = RequestMethod.GET)
    public Result getForm(@ApiParam(value = "???????????????id" ,required = true)String tableId,
                          @ApiParam(value = "????????????" ,required = true)String tableName){
        if (StrUtil.isBlank(tableName)){
            return Result.error("???????????????");
        }
        Map<String, Object> applyForm = fileService.getApplyForm(tableId, tableName);
        return Result.ok(applyForm);
    }

}
